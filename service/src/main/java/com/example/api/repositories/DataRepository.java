package com.example.api.repositories;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for accessing data in DynamoDB.
 */
public class DataRepository {
    private static final Logger logger = LoggerFactory.getLogger(DataRepository.class);
    
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<DataItem> table;
    
    /**
     * Create a new DataRepository.
     */
    public DataRepository() {
        // Get table name from environment variable
        String tableName = System.getenv("TABLE_NAME");
        if (tableName == null || tableName.isEmpty()) {
            tableName = "dev-api-table"; // Default for local testing
        }
        
        // Create DynamoDB client
        DynamoDbClient ddbClient = DynamoDbClient.builder()
            .region(Region.of(System.getenv("AWS_REGION")))
            .build();
        
        // Create enhanced client
        enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddbClient)
            .build();
        
        // Create table mapping
        table = enhancedClient.table(tableName, TableSchema.fromBean(DataItem.class));
        
        logger.info("Initialized DataRepository with table: {}", tableName);
    }
    
    /**
     * Get public data that's available to all users.
     * 
     * @return Map of public data
     */
    public Map<String, String> getPublicData() {
        logger.info("Getting public data");
        
        try {
            // In a real implementation, this would query DynamoDB for public data
            // For this example, we'll return static data
            Map<String, String> publicData = new HashMap<>();
            publicData.put("item1", "Public information 1");
            publicData.put("item2", "Public information 2");
            publicData.put("item3", "Public information 3");
            
            return publicData;
        } catch (Exception e) {
            logger.error("Error getting public data", e);
            throw new RuntimeException("Failed to get public data", e);
        }
    }
    
    /**
     * Get protected data that's only available to authenticated users.
     * 
     * @param userId The ID of the user requesting the data
     * @return Map of protected data
     */
    public Map<String, String> getProtectedData(String userId) {
        logger.info("Getting protected data for user: {}", userId);
        
        try {
            // In a real implementation, this would query DynamoDB for user-specific data
            // For this example, we'll return static data
            Map<String, String> protectedData = new HashMap<>();
            protectedData.put("item1", "Protected information 1 for user " + userId);
            protectedData.put("item2", "Protected information 2 for user " + userId);
            protectedData.put("item3", "Protected information 3 for user " + userId);
            
            return protectedData;
        } catch (Exception e) {
            logger.error("Error getting protected data for user: {}", userId, e);
            throw new RuntimeException("Failed to get protected data", e);
        }
    }
    
    /**
     * Save an item to DynamoDB.
     * 
     * @param item The item to save
     */
    public void saveItem(DataItem item) {
        logger.info("Saving item with ID: {}", item.getId());
        
        try {
            table.putItem(item);
        } catch (Exception e) {
            logger.error("Error saving item: {}", item.getId(), e);
            throw new RuntimeException("Failed to save item", e);
        }
    }
    
    /**
     * Get an item by ID.
     * 
     * @param id The ID of the item to get
     * @return The item, or empty if not found
     */
    public Optional<DataItem> getItem(String id) {
        logger.info("Getting item with ID: {}", id);
        
        try {
            Key key = Key.builder()
                .partitionValue("ITEM#" + id)
                .sortValue("METADATA")
                .build();
            
            DataItem item = table.getItem(key);
            return Optional.ofNullable(item);
        } catch (Exception e) {
            logger.error("Error getting item: {}", id, e);
            throw new RuntimeException("Failed to get item", e);
        }
    }
}
