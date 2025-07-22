# API Documentation

## Overview

This API provides CRUD operations for managing items in a serverless environment using AWS services.

## API Definition

The API is defined using OpenAPI 3.0.3 specification in `src/main/resources/openapi.yaml`.

## Endpoints

### List Items

```http
GET /items
```

Query Parameters:
- `limit` (optional): Maximum number of items to return (1-100, default: 20)

Response:
```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string",
    "createdAt": "string",
    "updatedAt": "string"
  }
]
```

### Get Item

```http
GET /items/{itemId}
```

Path Parameters:
- `itemId`: ID of the item to retrieve

Response:
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### Create Item

```http
POST /items
```

Request Body:
```json
{
  "name": "string",
  "description": "string"
}
```

Response:
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### Update Item

```http
PUT /items/{itemId}
```

Path Parameters:
- `itemId`: ID of the item to update

Request Body:
```json
{
  "name": "string",
  "description": "string"
}
```

Response:
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### Delete Item

```http
DELETE /items/{itemId}
```

Path Parameters:
- `itemId`: ID of the item to delete

Response: 204 No Content

## Authentication

The API uses Amazon Cognito for authentication. Include the JWT token in the Authorization header:

```http
Authorization: Bearer <token>
```

## Error Responses

The API returns standard HTTP status codes and JSON error responses:

```json
{
  "code": "string",
  "message": "string"
}
```

Common error codes:
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error

## Rate Limiting

- Default rate limit: 1000 requests per second
- Burst limit: 2000 requests

## Data Models

### Item

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "createdAt": "string",
  "updatedAt": "string"
}
```

### NewItem

```json
{
  "name": "string",
  "description": "string"
}
```

### Error

```json
{
  "code": "string",
  "message": "string"
}
```

## Code Generation

The API models are automatically generated using the OpenAPI Generator Maven plugin:

```bash
cd model
mvn clean generate-sources
```

Generated code is placed in `src/gen/java/main/`.

## Testing the API

### Using curl

List items:
```bash
curl -H "Authorization: Bearer <token>" \
  https://api.example.com/items
```

Create item:
```bash
curl -X POST \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Item","description":"Test Description"}' \
  https://api.example.com/items
```

### Using Postman

1. Import the OpenAPI definition
2. Set up an environment with:
   - `baseUrl`: Your API endpoint
   - `token`: Your Cognito token

## Versioning

The API uses semantic versioning (MAJOR.MINOR.PATCH):
- MAJOR: Breaking changes
- MINOR: New features (backwards compatible)
- PATCH: Bug fixes (backwards compatible)

## Support

For API issues or questions:
1. Check the documentation
2. Review CloudWatch logs
3. Contact the development team
