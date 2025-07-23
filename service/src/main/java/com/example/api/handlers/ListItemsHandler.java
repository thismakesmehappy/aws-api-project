package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.service.ItemService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

/**
 * Handler for the GET /items endpoint.
 */
public class ListItemsHandler extends BaseHandler {
    
    private final ItemService itemService;
    
    public ListItemsHandler() {
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
            
            return createSuccessResponse(200, itemService.listItems(limit));
        } catch (Exception e) {
            logger.error("Error listing items", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error listing items");
        }
    }
}
