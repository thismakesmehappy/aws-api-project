package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for the GET /public endpoint.
 */
public class GetPublicDataHandler extends BaseHandler {
    
    @Override
    public boolean requiresAuthentication() {
        return false; // This is a public endpoint
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "This is public data that anyone can access");
            response.put("timestamp", Instant.now().toString());
            
            return createSuccessResponse(200, response);
        } catch (Exception e) {
            logger.error("Error getting public data", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error retrieving public data");
        }
    }
}
