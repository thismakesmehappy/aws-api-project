#!/bin/bash
# Local pipeline script to simulate the CI/CD pipeline locally
# This helps test changes before pushing to GitHub

set -e  # Exit on any error

echo "🚀 Starting local pipeline simulation..."

# Set AWS region to us-east-1
export AWS_REGION=us-east-1
echo "🌎 Using AWS region: $AWS_REGION"

# Step 1: Install dependencies
echo "📦 Installing dependencies..."
npm run install:all

# Step 2: Run service tests
echo "🧪 Running service tests..."
npm run test:service

# Step 3: Run integration tests
echo "🧪 Running integration tests..."
npm run test:integration

# Step 4: Build service
echo "🔨 Building service..."
npm run build:service

# Step 5: Check for CDK differences in dev environment
echo "🔍 Checking for infrastructure changes in dev environment..."
npm run diff:dev

# Ask if user wants to deploy to dev
read -p "Do you want to deploy to dev environment? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "🚀 Deploying to dev environment..."
    npm run deploy:dev
    
    # Ask if user wants to deploy to staging
    read -p "Do you want to deploy to staging environment? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]
    then
        echo "🚀 Deploying to staging environment..."
        npm run deploy:staging
        
        # Ask if user wants to deploy to prod
        read -p "Do you want to deploy to production environment? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]
        then
            echo "🚀 Deploying to production environment..."
            npm run deploy:prod
        else
            echo "⏭️ Skipping production deployment."
        fi
    else
        echo "⏭️ Skipping staging deployment."
    fi
else
    echo "⏭️ Skipping deployments."
fi

echo "✅ Local pipeline simulation completed!"
