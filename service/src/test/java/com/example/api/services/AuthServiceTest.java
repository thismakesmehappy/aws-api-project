package com.example.api.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AuthService class.
 */
public class AuthServiceTest {
    
    private AuthService authService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set environment variables for testing
        System.setProperty("USER_POOL_ID", "test-user-pool");
        System.setProperty("CLIENT_ID", "test-client");
        
        // Create an auth service instance
        authService = new AuthService();
    }
    
    @Test
    public void testExtractToken_WithValidHeader() {
        // Create a request with an Authorization header
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer test-token");
        event.setHeaders(headers);
        
        // Call the method
        Optional<String> token = authService.extractToken(event);
        
        // Verify the result
        assertTrue(token.isPresent());
        assertEquals("test-token", token.get());
    }
    
    @Test
    public void testExtractToken_WithLowercaseHeader() {
        // Create a request with a lowercase authorization header
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer test-token");
        event.setHeaders(headers);
        
        // Call the method
        Optional<String> token = authService.extractToken(event);
        
        // Verify the result
        assertTrue(token.isPresent());
        assertEquals("test-token", token.get());
    }
    
    @Test
    public void testExtractToken_WithNoHeader() {
        // Create a request with no Authorization header
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        
        // Call the method
        Optional<String> token = authService.extractToken(event);
        
        // Verify the result
        assertFalse(token.isPresent());
    }
    
    @Test
    public void testExtractToken_WithInvalidHeader() {
        // Create a request with an invalid Authorization header
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Invalid test-token");
        event.setHeaders(headers);
        
        // Call the method
        Optional<String> token = authService.extractToken(event);
        
        // Verify the result
        assertFalse(token.isPresent());
    }
    
    @Test
    public void testValidateToken() {
        // In a real test, you would use a valid JWT token
        // For this example, we'll use a simple token since our implementation is mocked
        String token = "header.payload.signature";
        
        // Call the method
        Optional<Map<String, String>> claims = authService.validateToken(token);
        
        // Verify the result
        assertTrue(claims.isPresent());
        assertEquals("user-123", claims.get().get("sub"));
    }
    
    @Test
    public void testValidateToken_WithInvalidToken() {
        // Call the method with an invalid token
        Optional<Map<String, String>> claims = authService.validateToken("invalid-token");
        
        // Verify the result
        assertFalse(claims.isPresent());
    }
    
    @Test
    public void testHasAccess() {
        // Call the method
        boolean hasAccess = authService.hasAccess("user-123", "resource-456");
        
        // Verify the result
        assertTrue(hasAccess);
    }
}
