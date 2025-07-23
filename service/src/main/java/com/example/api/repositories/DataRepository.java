package com.example.api.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Repository for accessing data.
 */
public class DataRepository {
    private static final Logger logger = LoggerFactory.getLogger(DataRepository.class);
    
    private final DynamoDbClient dynamoDbClient;
    
    public DataRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }
    
    /**
     * Get public data.
     * 
     * @return public data
     */
    public String getPublicData() {
        logger.info("Getting public data");
        return "This is public data that anyone can access";
    }
    
    /**
     * Get protected data.
     * 
     * @return protected data
     */
    public String getProtectedData() {
        logger.info("Getting protected data");
        return "This is protected data that only authenticated users can access";
    }
}
