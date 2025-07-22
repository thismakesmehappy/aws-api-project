# Development Guide

This guide provides detailed information for developers working on the AWS API Project.

## Local Development Setup

1. **Install Prerequisites**
   - Node.js 16+
   - Java 11+
   - Maven
   - AWS CLI

2. **Configure AWS Credentials**
   ```bash
   aws configure
   ```
   Use credentials with appropriate permissions for development.

3. **Install Project Dependencies**
   ```bash
   npm run install:all
   ```

## API Development

### Modifying the API Definition

1. Edit the OpenAPI definition in `model/src/main/resources/openapi.yaml`
2. Regenerate the models:
   ```bash
   cd model
   mvn clean generate-sources
   ```
3. Update the service implementation in `service/src/main/java/com/example/api`

### Adding a New Endpoint

1. Add the endpoint definition to `openapi.yaml`:
   ```yaml
   /new-endpoint:
     get:
       summary: New endpoint description
       operationId: newEndpoint
       responses:
         '200':
           description: Success response
           content:
             application/json:
               schema:
                 $ref: '#/components/schemas/NewResponse'
   ```

2. Regenerate the models:
   ```bash
   cd model
   mvn clean generate-sources
   ```

3. Implement the endpoint in `ApiHandler.java`:
   ```java
   private APIGatewayProxyResponseEvent handleNewEndpoint(APIGatewayProxyRequestEvent input) {
       // Implementation
   }
   ```

4. Add tests in `service/src/test/java/com/example/api`

## Testing

### Unit Tests

```bash
# Run service unit tests
cd service
mvn test
```

### Integration Tests

```bash
# Run integration tests
cd integration-tests
npm test
```

### Local Testing Pipeline

```bash
./scripts/local-pipeline.sh
```

## Best Practices

### API Design
- Use consistent naming conventions
- Follow REST principles
- Include comprehensive documentation
- Use appropriate HTTP methods and status codes

### Code Style
- Follow Java coding conventions
- Write clear, self-documenting code
- Include JavaDoc comments for public methods
- Keep methods focused and concise

### Testing
- Write unit tests for all business logic
- Include integration tests for all endpoints
- Test error cases and edge conditions
- Use meaningful test names and descriptions

### Security
- Always validate input
- Use appropriate authentication
- Follow least privilege principle
- Keep dependencies updated

## Troubleshooting

### Common Issues

1. **Maven Build Fails**
   ```bash
   mvn clean install -X
   ```
   Check for dependency conflicts or missing repositories.

2. **Local Tests Fail**
   - Verify AWS credentials
   - Check DynamoDB local setup
   - Verify environment variables

3. **API Generation Issues**
   - Clear generated sources:
     ```bash
     cd model
     mvn clean
     ```
   - Validate OpenAPI syntax
   - Check for circular references

### Debugging

1. **Local API Testing**
   ```bash
   curl -v http://localhost:3000/items
   ```

2. **Lambda Logs**
   ```bash
   aws logs get-log-events --log-group-name /aws/lambda/your-function
   ```

3. **CDK Deployment**
   ```bash
   npx cdk diff
   ```

## Infrastructure

### Local Resources

The project uses local resources for development:
- DynamoDB Local
- Local API Gateway emulator

### AWS Resources

Production infrastructure includes:
- Lambda functions
- DynamoDB tables
- API Gateway
- Cognito User Pool
- CloudWatch Logs
- IAM roles and policies

## Monitoring and Logging

### CloudWatch Logs

Access Lambda logs:
```bash
aws logs get-log-events \
  --log-group-name /aws/lambda/your-function \
  --log-stream-name stream-name
```

### Metrics

Monitor using CloudWatch metrics:
- API Gateway requests
- Lambda execution times
- DynamoDB throughput
- Error rates

## Deployment

### Manual Deployment

```bash
# Deploy to dev
npm run deploy:dev

# Check deployment status
aws cloudformation describe-stacks \
  --stack-name DevApiStack
```

### Rollback

```bash
# Rollback dev deployment
npm run destroy:dev

# Deploy previous version
npm run deploy:dev -- --previous-version
```
