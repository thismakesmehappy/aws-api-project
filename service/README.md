# API Service Implementation with Java and AWS Coral

This directory contains the implementation of our API service using Java with AWS Coral.

## What is AWS Coral?

AWS Coral is a framework for building serverless applications on AWS. It provides a type-safe way to implement APIs defined in Smithy models, with built-in support for AWS Lambda and other AWS services.

## Benefits of Using Coral with Java

1. **Type Safety**: Fully typed implementation based on your Smithy model
2. **Middleware**: Built-in middleware for common tasks like validation and error handling
3. **Testing**: Easy to test with mock implementations
4. **AWS Integration**: Seamless integration with AWS services
5. **Performance**: Java's performance benefits for Lambda functions
6. **Mature Ecosystem**: Access to the rich Java ecosystem of libraries

## Project Structure

```
service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── api/
│   │   │               ├── ApiHandler.java         # Main Lambda handler
│   │   │               ├── handlers/               # API operation handlers
│   │   │               ├── middleware/             # Custom middleware
│   │   │               ├── repositories/           # Data access layer
│   │   │               └── services/               # Business logic
│   │   └── resources/
│   │       └── logback.xml                         # Logging configuration
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── api/                        # Unit tests
└── pom.xml                                         # Maven configuration
```

## Getting Started

1. Install dependencies:
   ```bash
   mvn clean install
   ```

2. Run tests:
   ```bash
   mvn test
   ```

3. Build the service:
   ```bash
   mvn package
   ```

## Example Handler

```java
// Example handler for the GetPublicData operation
package com.example.api.handlers;

import com.example.api.model.GetPublicDataOutput;
import software.amazon.coral.service.Coral;
import software.amazon.coral.service.CoralContext;

import java.time.Instant;

public class GetPublicDataHandler {
    public GetPublicDataOutput handle(CoralContext context) {
        return GetPublicDataOutput.builder()
            .message("This is public data that anyone can access")
            .timestamp(Instant.now())
            .build();
    }
}
```

## Authentication and Authorization

Our service uses Amazon Cognito for authentication:

1. **Public Endpoints**: No authentication required
2. **Protected Endpoints**: Require a valid JWT token from Cognito
3. **Authorization**: Uses Cognito groups and custom claims for fine-grained access control

Example protected handler:

```java
// Example handler for the GetProtectedData operation
package com.example.api.handlers;

import com.example.api.model.GetProtectedDataOutput;
import software.amazon.coral.service.Coral;
import software.amazon.coral.service.CoralContext;
import software.amazon.coral.service.exceptions.UnauthorizedException;

import java.time.Instant;

public class GetProtectedDataHandler {
    public GetProtectedDataOutput handle(CoralContext context) {
        // The user is already authenticated by Cognito
        String userId = context.getAuth().getClaims().get("sub");
        
        if (userId == null) {
            throw new UnauthorizedException("User ID not found in token");
        }
        
        return GetProtectedDataOutput.builder()
            .message("This is protected data that only authenticated users can access")
            .userId(userId)
            .timestamp(Instant.now())
            .build();
    }
}
```

## Local Testing

You can test the API locally using the AWS SAM CLI:

```bash
# Start local API
sam local start-api

# Test public endpoint
curl http://localhost:3000/public

# Test protected endpoint (requires auth token)
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:3000/protected
```

## Deployment

The service is deployed automatically as part of the CDK infrastructure:

```bash
# Deploy to development
cd ../infra
npx cdk deploy DevApiStack

# Deploy to production
npx cdk deploy ProdApiStack
```
## Testing with Authentication

### Local Testing

You can test the API locally using the provided Express server:

```bash
# Start the local server
cd ../local-dev
npm start

# Test public endpoint
curl http://localhost:3000/public

# Test protected endpoint with a test token
curl -H "Authorization: Bearer test-token" http://localhost:3000/protected
```

### AWS Testing

To test the deployed API on AWS:

1. Get your Cognito User Pool and Client IDs:
   ```bash
   aws cloudformation describe-stacks --stack-name DevApiStack --query "Stacks[0].Outputs[?OutputKey=='UserPoolId' || OutputKey=='UserPoolClientId']"
   ```

2. Register a user:
   ```bash
   aws cognito-idp sign-up \
     --client-id YOUR_CLIENT_ID \
     --username your-email@example.com \
     --password YourPassword123!
   ```

3. Confirm the user:
   ```bash
   aws cognito-idp confirm-sign-up \
     --client-id YOUR_CLIENT_ID \
     --username your-email@example.com \
     --confirmation-code 123456
   ```

4. Get an authentication token:
   ```bash
   aws cognito-idp initiate-auth \
     --client-id YOUR_CLIENT_ID \
     --auth-flow USER_PASSWORD_AUTH \
     --auth-parameters USERNAME=your-email@example.com,PASSWORD=YourPassword123!
   ```

5. Use the token to call the protected endpoint:
   ```bash
   # Get your API URL from the CloudFormation outputs
   API_URL=$(aws cloudformation describe-stacks --stack-name DevApiStack --query "Stacks[0].Outputs[?OutputKey=='ApiUrl'].OutputValue" --output text)
   
   # Call the protected endpoint
   curl -H "Authorization: Bearer YOUR_ID_TOKEN" $API_URL/protected
   ```
