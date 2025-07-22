package com.example.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the ApiHandler class.
 */
public class ApiHandlerTest {
    
    private ApiHandler handler;
    
    @Mock
    private Context context;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ApiHandler();
    }
    
    @Test
    public void testPublicEndpoint() {
        // Create a request to the public endpoint
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/public");
        
        // Call the handler
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        
        // Verify the response
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("public data"));
    }
    
    @Test
    public void testProtectedEndpointWithoutAuth() {
        // Create a request to the protected endpoint without auth
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/protected");
        
        // Call the handler
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        
        // Verify the response
        assertEquals(401, response.getStatusCode());
        assertTrue(response.getBody().contains("Unauthorized"));
    }
    
    @Test
    public void testProtectedEndpointWithAuth() {
        // Create a request to the protected endpoint with auth
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/protected");
        
        // Add Authorization header
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer test-token");
        request.setHeaders(headers);
        
        // Call the handler
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        
        // Verify the response
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("protected data"));
    }
    
    @Test
    public void testNotFoundEndpoint() {
        // Create a request to a non-existent endpoint
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/nonexistent");
        
        // Call the handler
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
        
        // Verify the response
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().contains("Not Found"));
    }
}
