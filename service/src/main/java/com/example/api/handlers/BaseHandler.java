package com.example.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.model.Error;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all API handlers.
 */
public abstract class BaseHandler implements RouteHandler {
    protected static final Logger logger = LoggerFactory.getLogger(BaseHandler.class);
    protected final ObjectMapper objectMapper;
    
    protected BaseHandler() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * By default, all handlers require authentication except those that explicitly override this method.
     */
    @Override
    public boolean requiresAuthentication() {
        return true;
    }
    
    /**
     * Creates a successful response with the given status code and body.
     */
    protected APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, Object body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        
        if (body != null) {
            try {
                response.setBody(objectMapper.writeValueAsString(body));
            } catch (Exception e) {
                logger.error("Error serializing response body", e);
                return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "Error serializing response");
            }
        }
        
        return response;
    }
    
    /**
     * Creates an error response with the given status code, error code, and message.
     */
    protected APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String code, String message) {
        Error error = new Error(code, message);
        return createSuccessResponse(statusCode, error);
    }
    
    /**
     * Creates a bad request response with the given message.
     */
    protected APIGatewayProxyResponseEvent createBadRequestResponse(String message) {
        return createErrorResponse(400, "BAD_REQUEST", message);
    }
    
    /**
     * Creates an unauthorized response.
     */
    protected APIGatewayProxyResponseEvent createUnauthorizedResponse() {
        return createErrorResponse(401, "UNAUTHORIZED", "Missing or invalid authentication token");
    }
    
    /**
     * Creates a not found response with the given message.
     */
    protected APIGatewayProxyResponseEvent createNotFoundResponse(String message) {
        return createErrorResponse(404, "NOT_FOUND", message);
    }
}
