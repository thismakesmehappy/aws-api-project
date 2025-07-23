package com.example.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.api.service.ItemService;
import com.example.api.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ApiHandlerTest {
    
    private ApiHandler apiHandler;
    
    @Mock
    private Context context;
    
    @Mock
    private AuthService authService;
    
    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test instance with mocked dependencies
        apiHandler = new ApiHandler(itemService, authService);
        
        // Setup default behavior for auth service
        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "user-123");
        
        when(authService.extractToken(any(APIGatewayProxyRequestEvent.class)))
            .thenReturn(Optional.of("valid-token"));
        when(authService.validateToken(anyString()))
            .thenReturn(Optional.of(claims));
    }

    @Test
    void testPublicEndpoint() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/public");
        request.setHttpMethod("GET");

        // Act
        APIGatewayProxyResponseEvent response = apiHandler.handleRequest(request, context);

        // Assert
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testProtectedEndpointWithoutAuth() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/items");
        request.setHttpMethod("GET");
        
        // Override default behavior for this test
        when(authService.extractToken(request)).thenReturn(Optional.empty());

        // Act
        APIGatewayProxyResponseEvent response = apiHandler.handleRequest(request, context);

        // Assert
        assertEquals(401, response.getStatusCode());
    }

    @Test
    void testProtectedEndpointWithAuth() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/items");
        request.setHttpMethod("GET");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid-token");
        request.setHeaders(headers);

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "user-123");
        
        when(authService.extractToken(request)).thenReturn(Optional.of("valid-token"));
        when(authService.validateToken("valid-token")).thenReturn(Optional.of(claims));

        // Act
        APIGatewayProxyResponseEvent response = apiHandler.handleRequest(request, context);

        // Assert
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testNotFoundEndpoint() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/nonexistent");
        request.setHttpMethod("GET");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid-token");
        request.setHeaders(headers);

        // Act
        APIGatewayProxyResponseEvent response = apiHandler.handleRequest(request, context);

        // Assert
        assertEquals(404, response.getStatusCode());
    }
}
