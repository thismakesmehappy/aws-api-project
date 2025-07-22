#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { ApiStack } from './stacks/api-stack';

const app = new cdk.App();

// Development environment
new ApiStack(app, 'DevApiStack', {
  env: { 
    account: process.env.CDK_DEFAULT_ACCOUNT, 
    region: 'us-east-1' 
  },
  stage: 'dev',
  budgetLimit: 4, // $4 monthly budget
  budgetEmail: 'bernardo+API_ALERT_DEV@thismakesmehappy.co', // Replace with your email
  description: 'Development environment for API project'
});

// Staging environment
new ApiStack(app, 'StagingApiStack', {
  env: { 
    account: process.env.CDK_DEFAULT_ACCOUNT, 
    region: 'us-east-1' 
  },
  stage: 'staging',
  budgetLimit: 4, // $4 monthly budget
  budgetEmail: 'bernardo+API_ALERT_STAGING@thismakesmehappy.co', // Replace with your email
  description: 'Staging environment for API project'
});

// Production environment
new ApiStack(app, 'ProdApiStack', {
  env: { 
    account: process.env.CDK_DEFAULT_ACCOUNT, 
    region: 'us-east-1' 
  },
  stage: 'prod',
  budgetLimit: 4, // $4 monthly budget
  budgetEmail: 'bernardo+API_ALERT_PROD@thismakesmehappy.co', // Replace with your email
  description: 'Production environment for API project'
});

app.synth();
