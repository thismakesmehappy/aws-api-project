#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting local development environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build the projects
echo "📦 Building projects..."
cd model
mvn clean install
cd ../service
mvn clean package
cd ..

# Start LocalStack
echo "🏃 Starting LocalStack..."
docker-compose up -d

# Wait for LocalStack to be ready
echo "⏳ Waiting for LocalStack to be ready..."
sleep 10

# Create DynamoDB table
echo "🗄️ Creating DynamoDB table..."
aws dynamodb create-table \
    --endpoint-url http://localhost:4566 \
    --table-name items-table-local \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

# Create Lambda function
echo "λ Creating Lambda function..."
aws lambda create-function \
    --endpoint-url http://localhost:4566 \
    --function-name api-handler \
    --runtime java11 \
    --handler com.example.api.ApiHandler::handleRequest \
    --memory-size 512 \
    --timeout 30 \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --zip-file fileb://service/target/api-service-1.0-SNAPSHOT.jar \
    --environment "Variables={TABLE_NAME=items-table-local}"

# Create API Gateway
echo "🌐 Creating API Gateway..."
aws apigateway create-rest-api \
    --endpoint-url http://localhost:4566 \
    --name api \
    --description "Local API" \
    > /tmp/api.json

API_ID=$(jq -r .id /tmp/api.json)
PARENT_ID=$(aws apigateway get-resources \
    --endpoint-url http://localhost:4566 \
    --rest-api-id $API_ID \
    | jq -r .items[0].id)

# Create /items resource
aws apigateway create-resource \
    --endpoint-url http://localhost:4566 \
    --rest-api-id $API_ID \
    --parent-id $PARENT_ID \
    --path-part items \
    > /tmp/items-resource.json

ITEMS_RESOURCE_ID=$(jq -r .id /tmp/items-resource.json)

# Create methods
for METHOD in GET POST
do
    aws apigateway put-method \
        --endpoint-url http://localhost:4566 \
        --rest-api-id $API_ID \
        --resource-id $ITEMS_RESOURCE_ID \
        --http-method $METHOD \
        --authorization-type NONE

    aws apigateway put-integration \
        --endpoint-url http://localhost:4566 \
        --rest-api-id $API_ID \
        --resource-id $ITEMS_RESOURCE_ID \
        --http-method $METHOD \
        --type AWS_PROXY \
        --integration-http-method POST \
        --uri arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:000000000000:function:api-handler/invocations
done

# Create /items/{itemId} resource
aws apigateway create-resource \
    --endpoint-url http://localhost:4566 \
    --rest-api-id $API_ID \
    --parent-id $ITEMS_RESOURCE_ID \
    --path-part "{itemId}" \
    > /tmp/item-resource.json

ITEM_RESOURCE_ID=$(jq -r .id /tmp/item-resource.json)

# Create methods for /items/{itemId}
for METHOD in GET PUT DELETE
do
    aws apigateway put-method \
        --endpoint-url http://localhost:4566 \
        --rest-api-id $API_ID \
        --resource-id $ITEM_RESOURCE_ID \
        --http-method $METHOD \
        --authorization-type NONE

    aws apigateway put-integration \
        --endpoint-url http://localhost:4566 \
        --rest-api-id $API_ID \
        --resource-id $ITEM_RESOURCE_ID \
        --http-method $METHOD \
        --type AWS_PROXY \
        --integration-http-method POST \
        --uri arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:000000000000:function:api-handler/invocations
done

# Deploy API
aws apigateway create-deployment \
    --endpoint-url http://localhost:4566 \
    --rest-api-id $API_ID \
    --stage-name local

echo "✅ Local development environment is ready!"
echo "🔗 API URL: http://localhost:4566/restapis/$API_ID/local/_user_request_"
echo ""
echo "Try it out:"
echo "curl -X GET http://localhost:4566/restapis/$API_ID/local/_user_request_/items"
echo "curl -X POST -H 'Content-Type: application/json' -d '{\"name\":\"Test Item\",\"description\":\"Test Description\"}' http://localhost:4566/restapis/$API_ID/local/_user_request_/items"

# Function to cleanup resources
cleanup() {
    echo "🧹 Cleaning up..."
    docker-compose down
    echo "✅ Cleanup complete!"
}

# Register cleanup function to run on script exit
trap cleanup EXIT

# Keep the script running
echo "📝 Logs will appear below. Press Ctrl+C to stop..."
docker-compose logs -f
