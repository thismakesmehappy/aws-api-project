package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.model.Item;
import com.example.api.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Handler for the PUT /items/{itemId} endpoint.
 */
public class UpdateItemHandler extends BaseHandler {
    
    private final ItemService itemService;
    
    public UpdateItemHandler() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.itemService = new ItemService(enhancedClient, System.getenv("TABLE_NAME"));
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            String itemId = extractItemId(input.getPath());
            Item updatedItem = objectMapper.readValue(input.getBody(), Item.class);
            
            if (updatedItem.name() == null || updatedItem.name().trim().isEmpty()) {
                return createBadRequestResponse("Name is required");
            }
            
            if (updatedItem.id() == null || updatedItem.id().trim().isEmpty()) {
                // Create a new Item with the correct ID since records are immutable
                updatedItem = new Item(
                    itemId,
                    updatedItem.name(),
                    updatedItem.description(),
                    updatedItem.createdAt(),
                    updatedItem.updatedAt()
                );
            } else if (!updatedItem.id().equals(itemId)) {
                return createBadRequestResponse("Item ID in path must match ID in body");
            }
            
            Item item = itemService.updateItem(itemId, updatedItem);
            if (item == null) {
                return createNotFoundResponse("Item not found");
            }
            
            return createSuccessResponse(200, item);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing request body", e);
            return createBadRequestResponse("Invalid request body");
        } catch (Exception e) {
            logger.error("Error updating item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error updating item");
        }
    }
    
    private String extractItemId(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
