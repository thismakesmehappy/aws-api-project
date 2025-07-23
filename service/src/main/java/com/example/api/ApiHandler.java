package com.example.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.handlers.*;
import com.example.api.model.Error;
import com.example.api.utils.HeaderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
    private final Map<RouteKey, RouteHandler> handlers;
    private final ObjectMapper objectMapper;

    public ApiHandler() {
        this.objectMapper = new ObjectMapper();
        this.handlers = new HashMap<>();
        
        // Register handlers for each route
        registerHandlers();
    }
    
    private void registerHandlers() {
        // Public endpoint
        handlers.put(new RouteKey("GET", "/public"), new GetPublicDataHandler());
        
        // Protected endpoint
        handlers.put(new RouteKey("GET", "/protected"), new GetProtectedDataHandler());
        
        // Item management endpoints
        handlers.put(new RouteKey("GET", "/items"), new ListItemsHandler());
        handlers.put(new RouteKey("POST", "/items"), new CreateItemHandler());
        handlers.put(new RouteKey("GET", "/items/{itemId}"), new GetItemHandler());
        handlers.put(new RouteKey("PUT", "/items/{itemId}"), new UpdateItemHandler());
        handlers.put(new RouteKey("DELETE", "/items/{itemId}"), new DeleteItemHandler());
        
        // Add new handlers here when adding new endpoints
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        logger.info("Received request: {}", input);

        try {
            String path = input.getPath();
            String httpMethod = input.getHttpMethod();
            
            // Find matching handler
            RouteHandler handler = findHandler(httpMethod, path);
            
            if (handler != null) {
                // Check if authentication is required
                if (handler.requiresAuthentication()) {
                    String token = HeaderUtils.extractBearerToken(input).orElse(null);
                    
                    if (token == null) {
                        return createUnauthorizedResponse();
                    }
                    
                    // In a real implementation, you would validate the token here
                }
                
                return handler.handleRequest(input, context);
            }
            
            // No handler found
            return createNotFoundResponse("Resource not found");
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return createErrorResponse(500, "INTERNAL_SERVER_ERROR", "An internal server error occurred");
        }
    }
    
    private RouteHandler findHandler(String method, String path) {
        // First try exact match
        RouteKey exactKey = new RouteKey(method, path);
        if (handlers.containsKey(exactKey)) {
            return handlers.get(exactKey);
        }
        
        // Try pattern matching for path parameters
        for (Map.Entry<RouteKey, RouteHandler> entry : handlers.entrySet()) {
            RouteKey key = entry.getKey();
            if (key.getMethod().equals(method) && pathMatches(key.getPath(), path)) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private boolean pathMatches(String pattern, String path) {
        // Convert route pattern with path parameters to regex
        String regex = pattern.replaceAll("\\{[^/]+\\}", "[^/]+");
        return Pattern.compile("^" + regex + "$").matcher(path).matches();
    }
    
    private APIGatewayProxyResponseEvent createUnauthorizedResponse() {
        Error error = new Error("UNAUTHORIZED", "Missing or invalid authentication token");
        return createResponse(401, error);
    }
    
    private APIGatewayProxyResponseEvent createNotFoundResponse(String message) {
        Error error = new Error("NOT_FOUND", message);
        return createResponse(404, error);
    }
    
    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String code, String message) {
        Error error = new Error(code, message);
        return createResponse(statusCode, error);
    }
    
    private APIGatewayProxyResponseEvent createResponse(int statusCode, Object body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        response.setHeaders(headers);
        
        if (body != null) {
            try {
                response.setBody(objectMapper.writeValueAsString(body));
            } catch (JsonProcessingException e) {
                logger.error("Error serializing response body", e);
                response.setBody("{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"Error serializing response\"}");
            }
        }
        
        return response;
    }
    
    // Helper class for route keys
    private static class RouteKey {
        private final String method;
        private final String path;
        
        public RouteKey(String method, String path) {
            this.method = method;
            this.path = path;
        }
        
        public String getMethod() {
            return method;
        }
        
        public String getPath() {
            return path;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RouteKey routeKey = (RouteKey) o;
            return method.equals(routeKey.method) && path.equals(routeKey.path);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(method, path);
        }
    }
}
