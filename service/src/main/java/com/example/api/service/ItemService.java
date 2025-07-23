package com.example.api.service;

import com.example.api.data.ItemEntity;
import com.example.api.model.Item;
import com.example.api.model.NewItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing items in the database.
 */
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    
    private final DynamoDbTable<ItemEntity> table;

    public ItemService(DynamoDbEnhancedClient dynamoDbClient, String tableName) {
        this.table = dynamoDbClient.table(tableName, TableSchema.fromBean(ItemEntity.class));
    }

    public List<Item> listItems(int limit) {
        logger.info("Listing items with limit: {}", limit);
        
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .limit(limit)
                .build();

        return table.scan(request)
                .items()
                .stream()
                .map(this::mapToModel)
                .toList();
    }

    public Item getItem(String id) {
        logger.info("Getting item with ID: {}", id);
        
        ItemEntity entity = table.getItem(getKeyById(id));
        return entity != null ? mapToModel(entity) : null;
    }

    public Item createItem(NewItem newItem) {
        logger.info("Creating new item: {}", newItem.name());
        
        OffsetDateTime now = OffsetDateTime.now();
        String id = UUID.randomUUID().toString();
        
        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName(newItem.name());
        entity.setDescription(newItem.description());
        entity.setCreatedAt(now.format(DATE_FORMATTER));
        entity.setUpdatedAt(now.format(DATE_FORMATTER));

        table.putItem(entity);
        
        return new Item(
            id,
            newItem.name(),
            newItem.description(),
            now,
            now
        );
    }

    public Item updateItem(String id, Item item) {
        logger.info("Updating item with ID: {}", id);
        
        // Check if item exists
        if (table.getItem(getKeyById(id)) == null) {
            return null;
        }

        OffsetDateTime now = OffsetDateTime.now();
        
        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName(item.name());
        entity.setDescription(item.description());
        entity.setCreatedAt(item.createdAt().format(DATE_FORMATTER));
        entity.setUpdatedAt(now.format(DATE_FORMATTER));

        table.putItem(entity);
        
        return item.withUpdatedAt(now);
    }

    public void deleteItem(String id) {
        logger.info("Deleting item with ID: {}", id);
        table.deleteItem(getKeyById(id));
    }

    private Key getKeyById(String id) {
        return Key.builder().partitionValue(id).build();
    }

    private Item mapToModel(ItemEntity entity) {
        return new Item(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            OffsetDateTime.parse(entity.getCreatedAt(), DATE_FORMATTER),
            OffsetDateTime.parse(entity.getUpdatedAt(), DATE_FORMATTER)
        );
    }
}
