package com.example.api.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for authentication and authorization.
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Extract the bearer token from the request.
     * 
     * @param event the API Gateway proxy request event
     * @return an Optional containing the bearer token, or empty if not found
     */
    public Optional<String> extractToken(APIGatewayProxyRequestEvent event) {
        Map<String, String> headers = event.getHeaders();
        if (headers == null) {
            return Optional.empty();
        }
        
        String authHeader = headers.get(AUTHORIZATION_HEADER);
        if (authHeader == null) {
            // Try case-insensitive lookup
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (AUTHORIZATION_HEADER.equalsIgnoreCase(entry.getKey())) {
                    authHeader = entry.getValue();
                    break;
                }
            }
        }
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }
        
        return Optional.empty();
    }
    
    /**
     * Validate the token and extract the claims.
     * 
     * @param token the JWT token
     * @return an Optional containing the claims, or empty if the token is invalid
     */
    public Optional<Map<String, String>> validateToken(String token) {
        // In a real implementation, you would validate the JWT token
        // For this example, we'll just return a mock user ID
        
        if (token != null && !token.equals("invalid-token")) {
            Map<String, String> claims = new HashMap<>();
            claims.put("sub", "user-123");
            return Optional.of(claims);
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if the user has access to the resource.
     * 
     * @param userId the user ID
     * @param resourceId the resource ID
     * @return true if the user has access, false otherwise
     */
    public boolean hasAccess(String userId, String resourceId) {
        // In a real implementation, you would check if the user has access to the resource
        // For this example, we'll just return true
        return true;
    }
}
