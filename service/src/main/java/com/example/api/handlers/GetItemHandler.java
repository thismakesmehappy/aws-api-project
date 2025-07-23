package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.model.Item;
import com.example.api.service.ItemService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Handler for the GET /items/{itemId} endpoint.
 */
public class GetItemHandler extends BaseHandler {
    
    private final ItemService itemService;
    
    public GetItemHandler() {
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
            Item item = itemService.getItem(itemId);
            
            if (item == null) {
                return createNotFoundResponse("Item not found");
            }
            
            return createSuccessResponse(200, item);
        } catch (Exception e) {
            logger.error("Error getting item", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error getting item");
        }
    }
    
    private String extractItemId(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
}
