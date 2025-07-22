import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as cognito from 'aws-cdk-lib/aws-cognito';
import * as cloudwatch from 'aws-cdk-lib/aws-cloudwatch';
import * as sns from 'aws-cdk-lib/aws-sns';
import * as subscriptions from 'aws-cdk-lib/aws-sns-subscriptions';
import * as budgets from 'aws-cdk-lib/aws-budgets';

interface ApiStackProps extends cdk.StackProps {
  stage: string;
  budgetLimit: number;
  budgetEmail: string;
}

export class ApiStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: ApiStackProps) {
    super(scope, id, props);

    const prefix = props.stage;

    // =========================================================================
    // COST CONTROL: AWS Budget
    // =========================================================================
    // Create a budget to track and alert on costs
    // This helps ensure we stay under our $4/month limit
    const budget = new budgets.CfnBudget(this, 'ApiProjectBudget', {
      budget: {
        budgetName: `${prefix}-api-project-budget`,
        budgetType: 'COST',
        timeUnit: 'MONTHLY',
        budgetLimit: {
          amount: props.budgetLimit,
          unit: 'USD',
        },
      },
      notificationsWithSubscribers: [
        {
          notification: {
            comparisonOperator: 'GREATER_THAN',
            threshold: 80, // Alert at 80% of budget
            notificationType: 'ACTUAL',
            thresholdType: 'PERCENTAGE',
          },
          subscribers: [
            {
              subscriptionType: 'EMAIL',
              address: props.budgetEmail,
            },
          ],
        },
        {
          notification: {
            comparisonOperator: 'GREATER_THAN',
            threshold: 100, // Alert at 100% of budget
            notificationType: 'ACTUAL',
            thresholdType: 'PERCENTAGE',
          },
          subscribers: [
            {
              subscriptionType: 'EMAIL',
              address: props.budgetEmail,
            },
          ],
        },
      ],
    });

    // =========================================================================
    // MONITORING: SNS Topic for Alarms
    // =========================================================================
    // Create an SNS topic for CloudWatch alarms
    // This centralizes all alarm notifications
    const alarmTopic = new sns.Topic(this, 'ApiAlarmTopic', {
      displayName: `${prefix}-api-alarms`,
    });

    // Add email subscription to the alarm topic
    alarmTopic.addSubscription(
      new subscriptions.EmailSubscription(props.budgetEmail)
    );

    // =========================================================================
    // DATABASE: DynamoDB Table
    // =========================================================================
    // Create a DynamoDB table with on-demand capacity
    // On-demand is cost-effective for low-traffic applications
    // as you only pay for what you use
    const table = new dynamodb.Table(this, 'ApiTable', {
      tableName: `${prefix}-api-table`,
      partitionKey: { name: 'pk', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'sk', type: dynamodb.AttributeType.STRING },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST, // On-demand capacity
      removalPolicy: props.stage === 'prod' 
        ? cdk.RemovalPolicy.RETAIN 
        : cdk.RemovalPolicy.DESTROY,
      pointInTimeRecovery: props.stage === 'prod', // Only enable for prod
    });

    // Add GSI for access patterns
    table.addGlobalSecondaryIndex({
      indexName: 'gsi1',
      partitionKey: { name: 'gsi1pk', type: dynamodb.AttributeType.STRING },
      sortKey: { name: 'gsi1sk', type: dynamodb.AttributeType.STRING },
    });

    // =========================================================================
    // AUTHENTICATION: Cognito User Pool
    // =========================================================================
    // Create a Cognito User Pool for authentication
    // This provides secure user management with minimal cost
    const userPool = new cognito.UserPool(this, 'ApiUserPool', {
      userPoolName: `${prefix}-api-users`,
      selfSignUpEnabled: true,
      autoVerify: { email: true },
      standardAttributes: {
        email: { required: true, mutable: true },
      },
      passwordPolicy: {
        minLength: 8,
        requireLowercase: true,
        requireUppercase: true,
        requireDigits: true,
        requireSymbols: true,
      },
      removalPolicy: cdk.RemovalPolicy.DESTROY, // For pet project, we can destroy
    });

    // Create a Cognito User Pool Client
    const userPoolClient = new cognito.UserPoolClient(this, 'ApiUserPoolClient', {
      userPool,
      userPoolClientName: `${prefix}-api-client`,
      generateSecret: false, // No secret for public clients
      authFlows: {
        userPassword: true,
        userSrp: true,
      },
    });

    // =========================================================================
    // API: Lambda Function with URL
    // =========================================================================
    // Create a Lambda function for the API
    // Using Java runtime with Coral implementation
    const apiFunction = new lambda.Function(this, 'ApiFunction', {
      functionName: `${prefix}-api-handler`,
      runtime: lambda.Runtime.JAVA_11,
      handler: 'com.example.api.ApiHandler::handleRequest',
      code: lambda.Code.fromAsset('../service/target/api-service-1.0-SNAPSHOT.jar'),
      memorySize: 512, // Java requires more memory than Node.js
      timeout: cdk.Duration.seconds(15), // Longer timeout for Java cold starts
      environment: {
        TABLE_NAME: table.tableName,
        USER_POOL_ID: userPool.userPoolId,
        CLIENT_ID: userPoolClient.userPoolClientId,
        STAGE: props.stage,
      },
    });

    // Grant the Lambda function read/write access to the DynamoDB table
    table.grantReadWriteData(apiFunction);

    // Create a Lambda Function URL (free alternative to API Gateway)
    const functionUrl = apiFunction.addFunctionUrl({
      authType: lambda.FunctionUrlAuthType.NONE, // Public access
      cors: {
        allowedOrigins: ['*'], // Allow all origins for testing
        allowedMethods: [lambda.HttpMethod.ALL],
        allowedHeaders: ['*'],
      },
    });

    // =========================================================================
    // MONITORING: CloudWatch Alarms
    // =========================================================================
    // Create alarms to monitor API usage and errors
    
    // 1. Lambda Error Rate Alarm
    new cloudwatch.Alarm(this, 'LambdaErrorAlarm', {
      metric: apiFunction.metricErrors({
        period: cdk.Duration.minutes(5),
      }),
      threshold: 1, // Alert on any error
      evaluationPeriods: 1,
      alarmDescription: `${prefix} API Lambda function is throwing errors`,
      actionsEnabled: true,
      alarmName: `${prefix}-api-lambda-errors`,
    }).addAlarmAction(new cdk.aws_cloudwatch_actions.SnsAction(alarmTopic));

    // 2. Lambda Throttling Alarm
    new cloudwatch.Alarm(this, 'LambdaThrottleAlarm', {
      metric: apiFunction.metricThrottles({
        period: cdk.Duration.minutes(5),
      }),
      threshold: 1, // Alert on any throttling
      evaluationPeriods: 1,
      alarmDescription: `${prefix} API Lambda function is being throttled`,
      actionsEnabled: true,
      alarmName: `${prefix}-api-lambda-throttles`,
    }).addAlarmAction(new cdk.aws_cloudwatch_actions.SnsAction(alarmTopic));

    // 3. DynamoDB Consumed Capacity Alarm
    // This helps monitor if we're approaching DynamoDB limits
    new cloudwatch.Alarm(this, 'DynamoDBConsumedCapacityAlarm', {
      metric: new cloudwatch.Metric({
        namespace: 'AWS/DynamoDB',
        metricName: 'ConsumedReadCapacityUnits',
        dimensionsMap: {
          TableName: table.tableName,
        },
        statistic: 'Sum',
        period: cdk.Duration.minutes(5),
      }),
      threshold: 240, // 80% of free tier daily limit over 5 minutes
      evaluationPeriods: 3,
      alarmDescription: `${prefix} DynamoDB table is approaching capacity limits`,
      actionsEnabled: true,
      alarmName: `${prefix}-dynamodb-capacity`,
    }).addAlarmAction(new cdk.aws_cloudwatch_actions.SnsAction(alarmTopic));

    // =========================================================================
    // OUTPUTS
    // =========================================================================
    // Export important resources for reference
    new cdk.CfnOutput(this, 'ApiUrl', {
      value: functionUrl.url,
      description: 'URL of the API endpoint',
      exportName: `${prefix}-api-url`,
    });

    new cdk.CfnOutput(this, 'UserPoolId', {
      value: userPool.userPoolId,
      description: 'Cognito User Pool ID',
      exportName: `${prefix}-user-pool-id`,
    });

    new cdk.CfnOutput(this, 'UserPoolClientId', {
      value: userPoolClient.userPoolClientId,
      description: 'Cognito User Pool Client ID',
      exportName: `${prefix}-user-pool-client-id`,
    });

    new cdk.CfnOutput(this, 'TableName', {
      value: table.tableName,
      description: 'DynamoDB Table Name',
      exportName: `${prefix}-table-name`,
    });
  }
}
