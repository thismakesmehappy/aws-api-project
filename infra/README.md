# API Project Infrastructure

This directory contains the AWS CDK code for deploying and managing the infrastructure for our API project.

## Architecture Overview

Our API is built using AWS serverless services to minimize costs while providing scalability:

- **AWS Lambda**: Serverless compute for API handlers
- **Lambda Function URLs**: Direct HTTP endpoints for Lambda functions (free alternative to API Gateway)
- **DynamoDB**: NoSQL database for data storage
- **Amazon Cognito**: User authentication and authorization
- **CloudWatch**: Monitoring, logging, and alarms
- **AWS Budgets**: Cost control and notifications

## Cost Control Strategy

This project is designed to stay within the AWS Free Tier and limit costs to under $4/month:

1. **AWS Budgets**: Set to $4/month with alerts at 80% and 100%
2. **CloudWatch Alarms**: Monitor usage and notify of potential cost increases
3. **Service Selection**: Using services with generous free tiers
4. **Resource Optimization**: Minimal Lambda memory, DynamoDB capacity

## Infrastructure Components

### API Layer
- **Lambda Function URLs**: Direct HTTP endpoints for API access
  - *Why*: No fixed costs compared to API Gateway
  - *Free Tier*: You only pay for Lambda invocations

### Compute Layer
- **AWS Lambda**: Serverless functions to handle API requests
  - *Why*: Pay-per-use model with generous free tier
  - *Free Tier*: 1M requests/month and 400,000 GB-seconds

### Data Layer
- **DynamoDB**: NoSQL database with on-demand capacity
  - *Why*: Serverless database with no minimum fees
  - *Free Tier*: 25 GB storage and 25 WCU/RCU

### Authentication
- **Amazon Cognito**: User management and authentication
  - *Why*: Managed auth service with free tier
  - *Free Tier*: 50,000 MAUs for users who sign in directly to Cognito

### Monitoring
- **CloudWatch**: Logs, metrics, and alarms
  - *Why*: Built-in monitoring for AWS services
  - *Free Tier*: 10 custom metrics and 10 alarms

## Environment Strategy

For this pet project, we're using a simplified environment strategy:

- **Same AWS Account**: Both dev and prod in the same account
- **Stack Separation**: Different CloudFormation stacks for dev/prod
- **Naming Convention**: Resources prefixed with `dev-` or `prod-`

## Deployment Process

1. **Development**: Deploy to dev stack for testing
2. **Testing**: Run integration tests against dev environment
3. **Production**: Deploy to prod stack when ready

## Cost Optimization Tips

1. **Lambda Optimization**:
   - Use minimal memory settings (128 MB) for simple functions
   - Set appropriate timeouts to avoid long-running functions

2. **DynamoDB Optimization**:
   - Use on-demand capacity for unpredictable workloads
   - Implement TTL for temporary data
   - Consider single-table design to minimize costs

3. **Monitoring Optimization**:
   - Use consolidated metrics where possible
   - Set up alarms only for critical metrics

## Getting Started

1. Install dependencies:
   ```bash
   cd infra
   npm install
   ```

2. Deploy the development stack:
   ```bash
   npx cdk deploy DevApiStack
   ```

3. Deploy the production stack:
   ```bash
   npx cdk deploy ProdApiStack
   ```

## Useful Commands

* `npm run build` - Compile TypeScript to JavaScript
* `npm run watch` - Watch for changes and compile
* `npm run test` - Perform the jest unit tests
* `npx cdk deploy` - Deploy this stack to your default AWS account/region
* `npx cdk diff` - Compare deployed stack with current state
* `npx cdk synth` - Emit the synthesized CloudFormation template
