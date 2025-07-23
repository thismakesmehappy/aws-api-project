package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Interface for API route handlers.
 */
public interface RouteHandler extends RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    /**
     * Determines if this route requires authentication.
     * 
     * @return true if authentication is required, false otherwise
     */
    boolean requiresAuthentication();
}
