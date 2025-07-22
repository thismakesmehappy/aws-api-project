# CI/CD Pipeline Documentation

This project uses GitHub Actions for continuous integration and continuous deployment (CI/CD). The pipeline automatically tests, builds, and deploys the application to different environments based on specific triggers.

## Pipeline Overview

The CI/CD pipeline follows this workflow:

1. **Build and Test**: Compiles the code and runs unit and integration tests locally.
2. **Deploy to Dev**: Deploys the application to the development environment and runs tests.
3. **Deploy to Staging**: If dev deployment is successful, deploys to the staging environment and runs tests.
   - If tests fail in staging, the deployment is automatically rolled back.
4. **Deploy to Production**: Only triggered manually after successful staging deployment.
   - If tests fail in production, the deployment is automatically rolled back.

## Environments

The project has three environments:

- **Dev**: Used for development and initial testing
- **Staging**: Pre-production environment for final testing
- **Production**: Live environment for end users

## How to Use the Pipeline

### Automatic Deployments

- Pushing to the `main` branch automatically triggers the pipeline to deploy to dev and then staging.
- Production deployments are never automatic and must be triggered manually.

### Manual Deployments

You can manually trigger deployments to any environment:

1. Go to the "Actions" tab in your GitHub repository
2. Select the "CI/CD Pipeline" workflow
3. Click "Run workflow"
4. Select the branch (usually `main`)
5. Choose the target environment (dev, staging, or prod)
6. Click "Run workflow"

### Rollback Process

- For staging and production environments, if the post-deployment tests fail, the pipeline automatically rolls back to the previous stable version.
- For manual rollbacks, you can re-run the workflow with a previous commit.

## Required Secrets

The following GitHub secrets need to be configured:

- `AWS_ACCESS_KEY_ID`: AWS access key with permissions to deploy resources
- `AWS_SECRET_ACCESS_KEY`: Corresponding AWS secret key
- `AWS_REGION`: AWS region to deploy resources to

## Local Development

For local development and testing:

```bash
# Install all dependencies
npm run install:all

# Start local development server
npm run start:local

# Run service tests
npm run test:service

# Run integration tests
npm run test:integration
```

## Deployment Commands

You can also deploy manually using npm scripts:

```bash
# Deploy to dev
npm run deploy:dev

# Deploy to staging
npm run deploy:staging

# Deploy to production
npm run deploy:prod
```

## Checking Deployment Differences

Before deploying, you can check what changes will be made:

```bash
# Check dev changes
npm run diff:dev

# Check staging changes
npm run diff:staging

# Check production changes
npm run diff:prod
```
