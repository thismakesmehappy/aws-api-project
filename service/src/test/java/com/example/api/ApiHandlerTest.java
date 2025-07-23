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

class ApiHandlerTest {
    
    private ApiHandler apiHandler;
    
    @Mock
    private Context context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test instance
        apiHandler = new ApiHandler();
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
        request.setPath("/protected");
        request.setHttpMethod("GET");

        // Act
        APIGatewayProxyResponseEvent response = apiHandler.handleRequest(request, context);

        // Assert
        assertEquals(401, response.getStatusCode());
    }

    @Test
    void testProtectedEndpointWithAuth() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPath("/protected");
        request.setHttpMethod("GET");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer valid-token");
        request.setHeaders(headers);

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
