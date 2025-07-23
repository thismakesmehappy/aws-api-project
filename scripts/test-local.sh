#!/bin/bash

# Exit on error
set -e

# Get the API ID from the first argument, or try to find it
API_ID=$1
if [ -z "$API_ID" ]; then
    API_ID=$(aws apigateway get-rest-apis --endpoint-url http://localhost:4566 | jq -r '.items[0].id')
    if [ -z "$API_ID" ]; then
        echo "❌ No API ID found. Make sure the local environment is running."
        exit 1
    fi
fi

BASE_URL="http://localhost:4566/restapis/$API_ID/local/_user_request_"

echo "🧪 Running API tests..."

# Test GET /items (should be empty initially)
echo "Testing GET /items..."
curl -s -X GET "$BASE_URL/items" | jq .

# Test POST /items
echo "Testing POST /items..."
RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"name":"Test Item","description":"Test Description"}' \
    "$BASE_URL/items")
echo $RESPONSE | jq .

# Extract the item ID from the response
ITEM_ID=$(echo $RESPONSE | jq -r .id)

# Test GET /items/{itemId}
echo "Testing GET /items/$ITEM_ID..."
curl -s -X GET "$BASE_URL/items/$ITEM_ID" | jq .

# Test PUT /items/{itemId}
echo "Testing PUT /items/$ITEM_ID..."
curl -s -X PUT \
    -H "Content-Type: application/json" \
    -d "{\"id\":\"$ITEM_ID\",\"name\":\"Updated Item\",\"description\":\"Updated Description\"}" \
    "$BASE_URL/items/$ITEM_ID" | jq .

# Test GET /items again (should show updated item)
echo "Testing GET /items..."
curl -s -X GET "$BASE_URL/items" | jq .

# Test DELETE /items/{itemId}
echo "Testing DELETE /items/$ITEM_ID..."
curl -s -X DELETE "$BASE_URL/items/$ITEM_ID"

# Test GET /items again (should be empty)
echo "Testing GET /items after delete..."
curl -s -X GET "$BASE_URL/items" | jq .

echo "✅ Tests completed!"
