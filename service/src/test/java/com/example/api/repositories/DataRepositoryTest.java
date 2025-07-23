package com.example.api.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.*;

class DataRepositoryTest {
    
    private DataRepository dataRepository;
    
    @Mock
    private DynamoDbClient dynamoDbClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataRepository = new DataRepository(dynamoDbClient);
    }

    @Test
    void testGetPublicData() {
        // Act
        String result = dataRepository.getPublicData();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("public"));
    }

    @Test
    void testGetProtectedData() {
        // Act
        String result = dataRepository.getProtectedData();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("protected"));
    }
}
