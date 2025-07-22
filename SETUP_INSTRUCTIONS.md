# AWS API Project Setup Instructions

This document provides step-by-step instructions for setting up the AWS API project with a CI/CD pipeline configured for the us-east-1 region.

## Step 1: Initialize Git Repository Locally

First, initialize a Git repository in your project folder:

```bash
cd aws-api-project
git init
git add .
git commit -m "Initial commit with CI/CD pipeline setup for us-east-1"
```

## Step 2: Create a GitHub Repository

1. Go to GitHub.com and log in to your account
2. Click the "+" icon in the top-right corner and select "New repository"
3. Name your repository (e.g., "aws-api-project")
4. Choose whether to make it public or private
5. Do NOT initialize with README, .gitignore, or license (since we're pushing an existing repository)
6. Click "Create repository"

## Step 3: Connect Local Repository to GitHub

GitHub will show instructions after creating the repository. Follow these commands:

```bash
git remote add origin https://github.com/YOUR-USERNAME/aws-api-project.git
git branch -M main
git push -u origin main
```

Replace `YOUR-USERNAME` with your actual GitHub username.

## Step 4: Get AWS Credentials

You'll need AWS credentials with permissions to deploy resources:

1. Log in to the AWS Management Console
2. Go to "IAM" (Identity and Access Management)
3. Create a new IAM user or use an existing one:
   - If creating a new user:
     - Click "Users" in the left sidebar, then "Add user"
     - Enter a username (e.g., "github-actions-deployer")
     - Select "Programmatic access"
     - Click "Next: Permissions"
     - Choose "Attach existing policies directly"
     - For testing, you can use "AdministratorAccess" (but for production, use more restricted permissions)
     - Click through to create the user
   - If using an existing user:
     - Click "Users" in the left sidebar
     - Select the user you want to use
     - Go to the "Security credentials" tab
     - Scroll to "Access keys" and click "Create access key"

4. After creating the access key, you'll see the "Access key ID" and "Secret access key"
   - **IMPORTANT**: This is the only time you'll see the secret key, so copy both values immediately
   - Store these securely for the next step

## Step 5: Add AWS Credentials to GitHub Secrets

1. Go to your GitHub repository
2. Click on "Settings" tab
3. In the left sidebar, click on "Secrets and variables" > "Actions"
4. Click "New repository secret"
5. Add the following secrets one by one:
   - Name: `AWS_ACCESS_KEY_ID`, Value: [Your AWS Access Key ID]
   - Name: `AWS_SECRET_ACCESS_KEY`, Value: [Your AWS Secret Access Key]

Note: You don't need to add AWS_REGION as a secret since we've hardcoded it to us-east-1 in all the configuration files.

## Step 6: Set Up GitHub Environments

For better control over deployments:

1. Go to your GitHub repository
2. Click on "Settings" tab
3. In the left sidebar, click on "Environments"
4. Create three environments:
   - Click "New environment"
   - Name: `dev`
   - Click "Configure environment"
   - No protection rules needed for dev
   
   Repeat for:
   - `staging` (optionally add protection rules like required reviewers)
   - `prod` (add protection rules like required reviewers and wait timer)

## Step 7: Run Local Pipeline for Testing

Before pushing changes that trigger the GitHub Actions workflow, test locally:

```bash
cd aws-api-project
./scripts/local-pipeline.sh
```

This will:
- Set the AWS region to us-east-1
- Install dependencies
- Run tests
- Build the service
- Show what changes would be made to your AWS environment
- Optionally deploy to dev, staging, and prod environments

## Step 8: Push Changes to GitHub to Trigger CI/CD

After testing locally, push your changes to GitHub:

```bash
git add .
git commit -m "Update configuration for deployment"
git push
```

This will trigger the GitHub Actions workflow, which will:
1. Build and test your code
2. Deploy to the dev environment in us-east-1
3. Run tests in the dev environment
4. Deploy to the staging environment in us-east-1
5. Run tests in the staging environment
6. Stop before production (requires manual approval)

## Step 9: Monitor the Workflow

1. Go to your GitHub repository
2. Click on the "Actions" tab
3. You should see your workflow running
4. Click on it to see detailed progress
5. If all steps succeed, your application will be deployed to dev and staging environments in us-east-1

## Step 10: Manually Deploy to Production

Once you're satisfied with the staging deployment:

1. Go to your GitHub repository
2. Click on the "Actions" tab
3. Click on "CI/CD Pipeline" in the left sidebar
4. Click "Run workflow"
5. Select the branch (usually `main`)
6. Choose "prod" as the environment
7. Click "Run workflow"

This will deploy your application to the production environment in us-east-1 with the same safeguards (automatic rollback if tests fail).

## Step 11: Verify Deployments

After deployments complete, verify your resources in the AWS Console:
1. Log in to AWS Console
2. Make sure you're in the us-east-1 region
3. Check the CloudFormation stacks
4. You should see `DevApiStack`, `StagingApiStack`, and `ProdApiStack` (if you deployed to production)
5. Check the outputs of these stacks to find your API URLs

Now you have a complete CI/CD pipeline set up with GitHub Actions that automatically tests and deploys your application to multiple environments in us-east-1 with rollback capabilities!
