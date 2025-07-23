package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.utils.HeaderUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for the GET /protected endpoint.
 */
public class GetProtectedDataHandler extends BaseHandler {
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // In a real implementation, you would extract user information from the token
            String userId = "user-123";
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "This is protected data that only authenticated users can access");
            response.put("userId", userId);
            response.put("timestamp", Instant.now().toString());
            
            return createSuccessResponse(200, response);
        } catch (Exception e) {
            logger.error("Error getting protected data", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error retrieving protected data");
        }
    }
}
