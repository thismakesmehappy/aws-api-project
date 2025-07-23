#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { DevApiStack } from '../lib/dev-api-stack';
import { StagingApiStack } from '../lib/staging-api-stack';
import { ProdApiStack } from '../lib/prod-api-stack';
import { IntegrationTestsStack } from '../lib/integration-tests-stack';
import { TestApiStack } from '../lib/test-api-stack';

const app = new cdk.App();

// Create the integration tests infrastructure
new IntegrationTestsStack(app, 'IntegrationTestsStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1',
  },
});

// Create the test API stack
new TestApiStack(app, 'TestApiStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1',
  },
});

// Create the API stacks
new DevApiStack(app, 'DevApiStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1',
  },
});

new StagingApiStack(app, 'StagingApiStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1',
  },
});

new ProdApiStack(app, 'ProdApiStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1',
  },
});
