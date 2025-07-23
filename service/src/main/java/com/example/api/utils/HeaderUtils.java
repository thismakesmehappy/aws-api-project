package com.example.api.utils;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for working with HTTP headers.
 */
public class HeaderUtils {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private HeaderUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Extracts the bearer token from the Authorization header.
     * 
     * @param event the API Gateway proxy request event
     * @return an Optional containing the bearer token, or empty if not found
     */
    public static Optional<String> extractBearerToken(APIGatewayProxyRequestEvent event) {
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
}
