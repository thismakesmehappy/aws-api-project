# AWS API Project

A serverless API project using AWS free tier services and OpenAPI for API definition.

## Project Structure

```
aws-api-project/
├── infra/               # AWS CDK infrastructure code
├── model/               # OpenAPI model definitions and generated code
├── service/            # Lambda service implementation
├── integration-tests/  # API integration tests
├── local-dev/         # Local development utilities
└── scripts/           # Development and deployment scripts
```

## Technology Stack

- **API Definition**: OpenAPI 3.0.3
- **Infrastructure**: AWS CDK
- **Runtime**: AWS Lambda with Java 11
- **Database**: Amazon DynamoDB
- **Authentication**: Amazon Cognito
- **CI/CD**: GitHub Actions

## Prerequisites

- Node.js 16+
- Java 11+
- Maven
- AWS CLI configured with appropriate credentials
- GitHub account (for CI/CD)

## Getting Started

1. **Install Dependencies**
   ```bash
   npm run install:all
   ```

2. **Run Tests**
   ```bash
   # Run service tests
   npm run test:service

   # Run integration tests
   npm run test:integration
   ```

3. **Local Development**
   ```bash
   npm run start:local
   ```

## Deployment

The project supports three environments: dev, staging, and production.

### Manual Deployment

```bash
# Deploy to dev
npm run deploy:dev

# Deploy to staging
npm run deploy:staging

# Deploy to production
npm run deploy:prod
```

### CI/CD Pipeline

The project uses GitHub Actions for automated deployments. See [CI/CD Documentation](.github/CI_CD_PIPELINE.md) for details.

## API Documentation

The API is defined using OpenAPI 3.0.3. See [API Documentation](model/README.md) for details.

## Development Guide

See [Development Guide](docs/DEVELOPMENT.md) for detailed instructions on:
- Local development setup
- Testing strategies
- Adding new endpoints
- Modifying the API definition
- Best practices

## Contributing

1. Create a feature branch from `main`
2. Make your changes
3. Run tests locally
4. Create a pull request

## License

MIT
