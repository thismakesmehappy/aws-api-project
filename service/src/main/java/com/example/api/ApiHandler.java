package com.example.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.model.Error;
import com.example.model.Item;
import com.example.model.NewItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.OffsetDateTime;
import java.util.*;

public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String TABLE_NAME = System.getenv("TABLE_NAME");
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.of(System.getenv("AWS_REGION")))
            .build();
    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        logger.info("Received request: {}", input);
        
        try {
            String path = input.getPath();
            String httpMethod = input.getHttpMethod();
            
            // Route the request based on path and method
            if (path.matches("/items/?") && "GET".equalsIgnoreCase(httpMethod)) {
                return listItems(input);
            } else if (path.matches("/items/?") && "POST".equalsIgnoreCase(httpMethod)) {
                return createItem(input);
            } else if (path.matches("/items/[^/]+/?") && "GET".equalsIgnoreCase(httpMethod)) {
                return getItem(input);
            } else if (path.matches("/items/[^/]+/?") && "PUT".equalsIgnoreCase(httpMethod)) {
                return updateItem(input);
            } else if (path.matches("/items/[^/]+/?") && "DELETE".equalsIgnoreCase(httpMethod)) {
                return deleteItem(input);
            } else {
                return createErrorResponse(404, "NOT_FOUND", "Resource not found");
            }
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "An internal server error occurred");
        }
    }
    
    private APIGatewayProxyResponseEvent listItems(APIGatewayProxyRequestEvent input) {
        try {
            // Extract query parameters
            Map<String, String> queryParams = input.getQueryStringParameters();
            int limit = 20; // Default limit
            
            if (queryParams != null && queryParams.containsKey("limit")) {
                try {
                    limit = Integer.parseInt(queryParams.get("limit"));
                    if (limit < 1 || limit > 100) {
                        limit = 20; // Reset to default if out of range
                    }
                } catch (NumberFormatException e) {
                    // Ignore and use default
                }
            }
            
            // Mock implementation - in a real app, you'd query DynamoDB
            List<Item> items = new ArrayList<>();
            for (int i = 1; i <= limit; i++) {
                Item item = new Item();
                item.setId(UUID.randomUUID().toString());
                item.setName("Item " + i);
                item.setDescription("Description for item " + i);
                item.setCreatedAt(OffsetDateTime.now().toString());
                item.setUpdatedAt(OffsetDateTime.now().toString());
                items.add(item);
            }
            
            return createSuccessResponse(200, items);
        } catch (Exception e) {
            logger.error("Error listing items", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error listing items");
        }
    }
    
    private APIGatewayProxyResponseEvent getItem(APIGatewayProxyRequestEvent input) {
        try {
            String itemId = extractItemId(input.getPath());
            
            // Mock implementation - in a real app, you'd query DynamoDB
            Item item = new Item();
            item.setId(itemId);
            item.setName("Sample Item");
            item.setDescription("This is a sample item");
            item.setCreatedAt(OffsetDateTime.now().minusDays(1).toString());
            item.setUpdatedAt(OffsetDateTime.now().toString());
            
            return createSuccessResponse(200, item);
        } catch (Exception e) {
            logger.error("Error getting item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error getting item");
        }
    }
    
    private APIGatewayProxyResponseEvent createItem(APIGatewayProxyRequestEvent input) {
        try {
            NewItem newItem = objectMapper.readValue(input.getBody(), NewItem.class);
            
            // Validate required fields
            if (newItem.getName() == null || newItem.getName().trim().isEmpty()) {
                return createErrorResponse(400, "BAD_REQUEST", "Name is required");
            }
            
            // Mock implementation - in a real app, you'd save to DynamoDB
            Item item = new Item();
            item.setId(UUID.randomUUID().toString());
            item.setName(newItem.getName());
            item.setDescription(newItem.getDescription());
            item.setCreatedAt(OffsetDateTime.now().toString());
            item.setUpdatedAt(OffsetDateTime.now().toString());
            
            return createSuccessResponse(201, item);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing request body", e);
            return createErrorResponse(400, "BAD_REQUEST", "Invalid request body");
        } catch (Exception e) {
            logger.error("Error creating item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error creating item");
        }
    }
    
    private APIGatewayProxyResponseEvent updateItem(APIGatewayProxyRequestEvent input) {
        try {
            String itemId = extractItemId(input.getPath());
            Item updatedItem = objectMapper.readValue(input.getBody(), Item.class);
            
            // Validate required fields
            if (updatedItem.getName() == null || updatedItem.getName().trim().isEmpty()) {
                return createErrorResponse(400, "BAD_REQUEST", "Name is required");
            }
            
            // Ensure ID in path matches ID in body, or set it if not provided
            if (updatedItem.getId() == null || updatedItem.getId().trim().isEmpty()) {
                updatedItem.setId(itemId);
            } else if (!updatedItem.getId().equals(itemId)) {
                return createErrorResponse(400, "BAD_REQUEST", "Item ID in path must match ID in body");
            }
            
            // Mock implementation - in a real app, you'd update in DynamoDB
            updatedItem.setUpdatedAt(OffsetDateTime.now().toString());
            
            return createSuccessResponse(200, updatedItem);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing request body", e);
            return createErrorResponse(400, "BAD_REQUEST", "Invalid request body");
        } catch (Exception e) {
            logger.error("Error updating item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error updating item");
        }
    }
    
    private APIGatewayProxyResponseEvent deleteItem(APIGatewayProxyRequestEvent input) {
        try {
            String itemId = extractItemId(input.getPath());
            
            // Mock implementation - in a real app, you'd delete from DynamoDB
            
            // Return 204 No Content for successful deletion
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(204);
            response.setHeaders(Map.of("Content-Type", "application/json"));
            return response;
        } catch (Exception e) {
            logger.error("Error deleting item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error deleting item");
        }
    }
    
    private String extractItemId(String path) {
        // Extract the item ID from the path
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    
    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, Object body) {
        try {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(statusCode);
            response.setHeaders(Map.of("Content-Type", "application/json"));
            response.setBody(objectMapper.writeValueAsString(body));
            return response;
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error serializing response");
        }
    }
    
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String code, String message) {
        try {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(statusCode);
            response.setHeaders(Map.of("Content-Type", "application/json"));
            
            Error error = new Error();
            error.setCode(code);
            error.setMessage(message);
            
            response.setBody(objectMapper.writeValueAsString(error));
            return response;
        } catch (JsonProcessingException e) {
            logger.error("Error serializing error response", e);
            
            // Fallback to simple error response
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(500);
            response.setHeaders(Map.of("Content-Type", "application/json"));
            response.setBody("{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"Error serializing error response\"}");
            return response;
        }
    }
}
