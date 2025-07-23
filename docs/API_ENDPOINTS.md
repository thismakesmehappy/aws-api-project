# API Endpoints Documentation

This document provides detailed information about the available API endpoints.

## Base URL

- **Development**: https://dev-api.example.com/v1
- **Production**: https://api.example.com/v1
- **Local**: http://localhost:3000

## Authentication

All protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer YOUR_TOKEN
```

For local development, any token value will work.

## Endpoints

### Public Endpoints

#### Get Public Data

```http
GET /public
```

**Response**:
```json
{
  "message": "This is public data that anyone can access",
  "timestamp": "2025-07-22T23:31:04.069Z"
}
```

### Protected Endpoints

#### Get Protected Data

```http
GET /protected
```

**Response**:
```json
{
  "message": "This is protected data that only authenticated users can access",
  "userId": "user-123",
  "timestamp": "2025-07-22T23:31:04.069Z"
}
```

### Item Management

#### List Items

```http
GET /items
```

**Query Parameters**:
- `limit` (optional): Maximum number of items to return (1-100, default: 20)

**Response**:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Item Name",
    "description": "Item Description",
    "createdAt": "2025-07-22T23:31:04.069Z",
    "updatedAt": "2025-07-22T23:31:04.069Z"
  }
]
```

#### Get Item by ID

```http
GET /items/{itemId}
```

**Path Parameters**:
- `itemId`: ID of the item to retrieve

**Response**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Item Name",
  "description": "Item Description",
  "createdAt": "2025-07-22T23:31:04.069Z",
  "updatedAt": "2025-07-22T23:31:04.069Z"
}
```

#### Create Item

```http
POST /items
```

**Request Body**:
```json
{
  "name": "New Item",
  "description": "This is a new item"
}
```

**Response**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "New Item",
  "description": "This is a new item",
  "createdAt": "2025-07-22T23:31:04.069Z",
  "updatedAt": "2025-07-22T23:31:04.069Z"
}
```

#### Update Item

```http
PUT /items/{itemId}
```

**Path Parameters**:
- `itemId`: ID of the item to update

**Request Body**:
```json
{
  "name": "Updated Item",
  "description": "This item has been updated"
}
```

**Response**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Updated Item",
  "description": "This item has been updated",
  "createdAt": "2025-07-22T23:31:04.069Z",
  "updatedAt": "2025-07-22T23:31:04.069Z"
}
```

#### Delete Item

```http
DELETE /items/{itemId}
```

**Path Parameters**:
- `itemId`: ID of the item to delete

**Response**: 204 No Content

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request

```json
{
  "code": "BAD_REQUEST",
  "message": "Error message describing the issue"
}
```

### 401 Unauthorized

```json
{
  "code": "UNAUTHORIZED",
  "message": "Missing authentication token"
}
```

### 404 Not Found

```json
{
  "code": "NOT_FOUND",
  "message": "Resource not found"
}
```

### 500 Internal Server Error

```json
{
  "code": "INTERNAL_SERVER_ERROR",
  "message": "An internal server error occurred"
}
```

## Testing with curl

### Local Testing

```bash
# Test public endpoint
curl http://localhost:3000/public

# Test protected endpoint
curl -H "Authorization: Bearer test-token" http://localhost:3000/protected

# List items
curl -H "Authorization: Bearer test-token" http://localhost:3000/items

# Create item
curl -X POST \
  -H "Authorization: Bearer test-token" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Item","description":"This is a test item"}' \
  http://localhost:3000/items

# Get item by ID
curl -H "Authorization: Bearer test-token" http://localhost:3000/items/ITEM_ID

# Update item
curl -X PUT \
  -H "Authorization: Bearer test-token" \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Item","description":"This item has been updated"}' \
  http://localhost:3000/items/ITEM_ID

# Delete item
curl -X DELETE \
  -H "Authorization: Bearer test-token" \
  http://localhost:3000/items/ITEM_ID
```

### AWS Testing

```bash
# Replace API_URL with your actual API URL
API_URL="https://dev-api.example.com/v1"

# Get a valid token from Cognito
TOKEN=$(aws cognito-idp initiate-auth \
  --client-id YOUR_CLIENT_ID \
  --auth-flow USER_PASSWORD_AUTH \
  --auth-parameters USERNAME=your-email@example.com,PASSWORD=YourPassword123! \
  --query "AuthenticationResult.IdToken" \
  --output text)

# Test protected endpoint
curl -H "Authorization: Bearer $TOKEN" $API_URL/protected

# List items
curl -H "Authorization: Bearer $TOKEN" $API_URL/items

# Create item
curl -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Item","description":"This is a test item"}' \
  $API_URL/items
```
