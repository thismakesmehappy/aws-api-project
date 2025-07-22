package com.example.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.repositories.DataRepository;
import com.example.api.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main Lambda handler for API requests.
 * This class serves as the entry point for Lambda Function URL invocations.
 */
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);
    
    private final DataRepository dataRepository;
    private final AuthService authService;
    
    public ApiHandler() {
        // Initialize dependencies
        this.dataRepository = new DataRepository();
        this.authService = new AuthService();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        logger.info("Processing request: {}", event.getPath());
        
        try {
            // Route the request based on the path
            String path = event.getPath();
            
            if ("/public".equals(path)) {
                return handlePublicRequest();
            } else if ("/protected".equals(path)) {
                return handleProtectedRequest(event);
            } else {
                return notFoundResponse();
            }
        } catch (Exception e) {
            logger.error("Error processing request", e);
            return errorResponse(e);
        }
    }
    
    /**
     * Handle requests to the public endpoint.
     */
    private APIGatewayProxyResponseEvent handlePublicRequest() {
        // Get public data from repository
        Map<String, String> publicData = dataRepository.getPublicData();
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "This is public data that anyone can access");
        responseBody.put("data", publicData);
        responseBody.put("timestamp", Instant.now().toString());
        
        return createResponse(200, responseBody);
    }
    
    /**
     * Handle requests to the protected endpoint.
     */
    private APIGatewayProxyResponseEvent handleProtectedRequest(APIGatewayProxyRequestEvent event) {
        // Extract and validate token
        Optional<String> tokenOpt = authService.extractToken(event);
        
        if (!tokenOpt.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Unauthorized - Missing token");
            return createResponse(401, responseBody);
        }
        
        // Validate token and get user ID
        Optional<Map<String, String>> claimsOpt = authService.validateToken(tokenOpt.get());
        
        if (!claimsOpt.isPresent()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Unauthorized - Invalid token");
            return createResponse(401, responseBody);
        }
        
        String userId = claimsOpt.get().get("sub");
        
        // Get protected data from repository
        Map<String, String> protectedData = dataRepository.getProtectedData(userId);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "This is protected data that only authenticated users can access");
        responseBody.put("userId", userId);
        responseBody.put("data", protectedData);
        responseBody.put("timestamp", Instant.now().toString());
        
        return createResponse(200, responseBody);
    }
    
    /**
     * Create a 404 Not Found response.
     */
    private APIGatewayProxyResponseEvent notFoundResponse() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Not Found");
        
        return createResponse(404, responseBody);
    }
    
    /**
     * Create an error response for exceptions.
     */
    private APIGatewayProxyResponseEvent errorResponse(Exception e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Internal Server Error");
        responseBody.put("error", e.getMessage());
        
        return createResponse(500, responseBody);
    }
    
    /**
     * Helper method to create API Gateway response with JSON body.
     */
    private APIGatewayProxyResponseEvent createResponse(int statusCode, Map<String, Object> body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        
        try {
            // Simple JSON serialization
            StringBuilder jsonBuilder = new StringBuilder("{");
            boolean first = true;
            
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                if (!first) {
                    jsonBuilder.append(",");
                }
                first = false;
                
                jsonBuilder.append("\"").append(entry.getKey()).append("\":");
                
                Object value = entry.getValue();
                if (value instanceof String) {
                    jsonBuilder.append("\"").append(value).append("\"");
                } else if (value instanceof Number || value instanceof Boolean) {
                    jsonBuilder.append(value);
                } else if (value instanceof Map) {
                    // Handle nested maps (simple implementation)
                    jsonBuilder.append("{");
                    boolean firstNested = true;
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nestedMap = (Map<String, Object>) value;
                    
                    for (Map.Entry<String, Object> nestedEntry : nestedMap.entrySet()) {
                        if (!firstNested) {
                            jsonBuilder.append(",");
                        }
                        firstNested = false;
                        
                        jsonBuilder.append("\"").append(nestedEntry.getKey()).append("\":\"")
                                   .append(nestedEntry.getValue()).append("\"");
                    }
                    
                    jsonBuilder.append("}");
                } else {
                    jsonBuilder.append("\"").append(value).append("\"");
                }
            }
            
            jsonBuilder.append("}");
            response.setBody(jsonBuilder.toString());
        } catch (Exception e) {
            logger.error("Error serializing response", e);
            response.setBody("{\"message\":\"Error creating response\"}");
        }
        
        return response;
    }
}
