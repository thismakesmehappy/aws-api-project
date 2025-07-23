# Guide for Adding New Endpoints

This document provides step-by-step instructions for adding new endpoints to the API.

## Overview

Our API uses a consistent approach for managing routes across both local development and AWS environments. The process involves:

1. Updating the OpenAPI specification
2. Creating a new handler class
3. Registering the handler in the ApiHandler
4. Testing locally
5. Deploying to AWS

## Step 1: Update the OpenAPI Specification

Add your new endpoint to the OpenAPI specification file at `/model/src/main/resources/openapi.yaml`:

```yaml
paths:
  # Existing paths...
  
  /new-endpoint:
    get:
      summary: Description of your new endpoint
      tags:
        - category
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/YourResponseModel'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/ServerError'
```

If your endpoint requires new data models, add them to the `components.schemas` section.

## Step 2: Create a New Handler Class

Create a new handler class in `/service/src/main/java/com/example/api/handlers/`:

```java
package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for the GET /new-endpoint endpoint.
 */
public class NewEndpointHandler extends BaseHandler {
    
    @Override
    public boolean requiresAuthentication() {
        return true; // Set to false for public endpoints
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // Your implementation here
            Map<String, Object> response = new HashMap<>();
            response.put("message", "This is your new endpoint");
            
            return createSuccessResponse(200, response);
        } catch (Exception e) {
            logger.error("Error in new endpoint", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error processing request");
        }
    }
}
```

## Step 3: Register the Handler in ApiHandler

Add your new handler to the `registerHandlers` method in `ApiHandler.java`:

```java
private void registerHandlers() {
    // Existing handlers...
    
    // Add your new handler
    handlers.put(new RouteKey("GET", "/new-endpoint"), new NewEndpointHandler());
}
```

## Step 4: Test Locally

1. Start the local development server:
   ```bash
   cd local-dev
   node server.js
   ```

2. Add your new endpoint to the local server in `server.js`:
   ```javascript
   // New endpoint
   app.get('/new-endpoint', authenticate, (req, res) => {
     res.json({
       message: "This is your new endpoint"
     });
   });
   ```

3. Test your endpoint with curl:
   ```bash
   curl -H "Authorization: Bearer test-token" http://localhost:3000/new-endpoint
   ```

## Step 5: Deploy to AWS

1. Build the service:
   ```bash
   cd service
   mvn clean package
   ```

2. Deploy using your infrastructure as code:
   ```bash
   cd ../infra
   cdk deploy
   ```

## Best Practices

1. **Consistent Naming**: Use consistent naming conventions for your endpoints, handlers, and models.

2. **Input Validation**: Always validate input data before processing.

3. **Error Handling**: Provide clear error messages and appropriate HTTP status codes.

4. **Documentation**: Update the API documentation in `/docs/API_ENDPOINTS.md` with your new endpoint.

5. **Testing**: Write unit tests for your new handler.

## Example: Adding a "Get User Profile" Endpoint

### 1. Update OpenAPI Specification

```yaml
paths:
  /users/profile:
    get:
      summary: Get the current user's profile
      tags:
        - users
      responses:
        '200':
          description: User profile
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserProfile'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '500':
          $ref: '#/components/responses/ServerError'

components:
  schemas:
    UserProfile:
      type: object
      properties:
        id:
          type: string
        username:
          type: string
        email:
          type: string
        createdAt:
          type: string
          format: date-time
```

### 2. Create Handler Class

```java
package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

public class GetUserProfileHandler extends BaseHandler {
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // In a real implementation, you would get the user ID from the token
            // and fetch the user profile from a database
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", "user-123");
            profile.put("username", "johndoe");
            profile.put("email", "john.doe@example.com");
            profile.put("createdAt", "2025-01-01T00:00:00Z");
            
            return createSuccessResponse(200, profile);
        } catch (Exception e) {
            logger.error("Error getting user profile", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error getting user profile");
        }
    }
}
```

### 3. Register Handler

```java
private void registerHandlers() {
    // Existing handlers...
    
    // Add user profile handler
    handlers.put(new RouteKey("GET", "/users/profile"), new GetUserProfileHandler());
}
```

### 4. Update Local Server

```javascript
// User profile endpoint
app.get('/users/profile', authenticate, (req, res) => {
  res.json({
    id: "user-123",
    username: "johndoe",
    email: "john.doe@example.com",
    createdAt: "2025-01-01T00:00:00Z"
  });
});
```

### 5. Update API Documentation

Add the new endpoint to `/docs/API_ENDPOINTS.md`.
