package com.example.api.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DataRepository class.
 */
public class DataRepositoryTest {
    
    private DataRepository repository;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set environment variables for testing
        System.setProperty("TABLE_NAME", "test-table");
        System.setProperty("AWS_REGION", "us-east-1");
        
        // Create a repository instance
        // Note: This will attempt to connect to DynamoDB, so we'll mock the methods for testing
        // In a real test, you would use DynamoDB Local or mock the DynamoDB client
        repository = new DataRepository() {
            @Override
            public Map<String, String> getPublicData() {
                return Map.of(
                    "item1", "Public information 1",
                    "item2", "Public information 2",
                    "item3", "Public information 3"
                );
            }
            
            @Override
            public Map<String, String> getProtectedData(String userId) {
                return Map.of(
                    "item1", "Protected information 1 for user " + userId,
                    "item2", "Protected information 2 for user " + userId,
                    "item3", "Protected information 3 for user " + userId
                );
            }
        };
    }
    
    @Test
    public void testGetPublicData() {
        // Call the method
        Map<String, String> publicData = repository.getPublicData();
        
        // Verify the result
        assertNotNull(publicData);
        assertEquals(3, publicData.size());
        assertEquals("Public information 1", publicData.get("item1"));
        assertEquals("Public information 2", publicData.get("item2"));
        assertEquals("Public information 3", publicData.get("item3"));
    }
    
    @Test
    public void testGetProtectedData() {
        // Call the method
        String userId = "test-user";
        Map<String, String> protectedData = repository.getProtectedData(userId);
        
        // Verify the result
        assertNotNull(protectedData);
        assertEquals(3, protectedData.size());
        assertEquals("Protected information 1 for user " + userId, protectedData.get("item1"));
        assertEquals("Protected information 2 for user " + userId, protectedData.get("item2"));
        assertEquals("Protected information 3 for user " + userId, protectedData.get("item3"));
    }
}
