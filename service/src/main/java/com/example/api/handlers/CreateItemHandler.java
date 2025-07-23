package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.model.Item;
import com.example.api.model.NewItem;
import com.example.api.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Handler for the POST /items endpoint.
 */
public class CreateItemHandler extends BaseHandler {
    
    private final ItemService itemService;
    
    public CreateItemHandler() {
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
            NewItem newItem = objectMapper.readValue(input.getBody(), NewItem.class);
            
            if (newItem.name() == null || newItem.name().trim().isEmpty()) {
                return createBadRequestResponse("Name is required");
            }
            
            Item item = itemService.createItem(newItem);
            return createSuccessResponse(201, item);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing request body", e);
            return createBadRequestResponse("Invalid request body");
        } catch (Exception e) {
            logger.error("Error creating item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error creating item");
        }
    }
}
