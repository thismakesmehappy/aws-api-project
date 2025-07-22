# AWS API Project

A serverless API project using AWS free tier services with clean separation of concerns.

## Project Overview

This project demonstrates how to build a serverless API on AWS with:

- **Cost Control**: Stays within the AWS Free Tier with budget alerts
- **Separation of Concerns**: Clean separation between infrastructure, models, and service code
- **Authentication**: Secure API endpoints with Amazon Cognito
- **Local Testing**: Test the API locally before deploying

## Project Structure

```
aws-api-project/
├── infra/              # Infrastructure as Code (AWS CDK)
├── model/              # API models (Smithy)
├── service/            # Service implementation (Java with AWS Coral)
├── integration-tests/  # Integration tests
└── local-dev/          # Local development utilities
```

## Technologies Used

- **Infrastructure**: AWS CDK (TypeScript)
- **API Definition**: Smithy
- **Service Implementation**: Java with AWS Coral
- **Database**: Amazon DynamoDB
- **Authentication**: Amazon Cognito
- **Compute**: AWS Lambda with Function URLs
- **Monitoring**: Amazon CloudWatch
- **Cost Control**: AWS Budgets

## Getting Started

### Prerequisites

- Node.js 18 or later
- AWS CLI configured with your AWS account
- AWS CDK installed (`npm install -g aws-cdk`)
- Java 11 or later
- Maven 3.6 or later

### Setup

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/aws-api-project.git
   cd aws-api-project
   ```

2. Install dependencies and build the project:
   ```bash
   # Install all dependencies and build Java service
   npm run install:all
   ```

3. Update the email address for budget alerts in `infra/src/app.ts`

### Local Development

1. Start the local development server:
   ```bash
   npm run start:local
   ```

2. Test the API locally:
   ```bash
   # Public endpoint
   curl http://localhost:3000/public
   
   # Protected endpoint (requires auth)
   curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:3000/protected
   ```

### Testing

1. Run unit tests for the Java service:
   ```bash
   npm run test:service
   ```

2. Run integration tests:
   ```bash
   npm run test:integration
   ```

### Deployment

1. Deploy to development:
   ```bash
   # This will build the Java service and deploy the infrastructure
   npm run deploy:dev
   ```

2. Deploy to production:
   ```bash
   # This will build the Java service and deploy the infrastructure
   npm run deploy:prod
   ```

## Authentication

The API uses Amazon Cognito for authentication:

1. Register a user:
   ```bash
   aws cognito-idp sign-up \
     --client-id YOUR_CLIENT_ID \
     --username your-email@example.com \
     --password YourPassword123!
   ```

2. Confirm the user:
   ```bash
   aws cognito-idp confirm-sign-up \
     --client-id YOUR_CLIENT_ID \
     --username your-email@example.com \
     --confirmation-code 123456
   ```

3. Get an authentication token:
   ```bash
   aws cognito-idp initiate-auth \
     --client-id YOUR_CLIENT_ID \
     --auth-flow USER_PASSWORD_AUTH \
     --auth-parameters USERNAME=your-email@example.com,PASSWORD=YourPassword123!
   ```
   
   The response will include an `IdToken` - this is what you'll use to authenticate API requests.

4. Use the token in your API requests:
   ```bash
   curl -H "Authorization: Bearer YOUR_ID_TOKEN" https://your-api-url/protected
   ```

5. For local testing:
   ```bash
   # The local implementation doesn't validate real tokens
   curl -H "Authorization: Bearer test-token" http://localhost:3000/protected
   ```

### Getting Your Cognito User Pool and Client IDs

After deploying your API, you can retrieve the User Pool ID and Client ID with:

```bash
aws cloudformation describe-stacks --stack-name DevApiStack --query "Stacks[0].Outputs[?OutputKey=='UserPoolId' || OutputKey=='UserPoolClientId']"
```

### Helper Script for Token Acquisition

For convenience, a helper script is provided to simplify token acquisition:

```bash
# Make the script executable if needed
chmod +x scripts/get-token.sh

# Get a token (creates user if needed)
./scripts/get-token.sh --username your-email@example.com --password YourPassword123!

# For production stack
./scripts/get-token.sh --stack ProdApiStack --username your-email@example.com --password YourPassword123!
```

The script will:
1. Get the User Pool and Client IDs from CloudFormation
2. Create and confirm the user if needed
3. Get an authentication token
4. Display the token and example curl command

## Cost Control

This project is designed to stay within the AWS Free Tier:

- **Budget Alert**: Set to $4/month with notifications at 80% and 100%
- **CloudWatch Alarms**: Monitor usage and alert on potential cost increases
- **Resource Optimization**: Optimized Lambda memory settings for Java runtime

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
