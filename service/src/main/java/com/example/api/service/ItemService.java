package com.example.api.service;

import com.example.api.data.ItemEntity;
import com.example.model.Item;
import com.example.model.NewItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final DynamoDbTable<ItemEntity> table;

    public ItemService(DynamoDbEnhancedClient dynamoDbClient, String tableName) {
        this.table = dynamoDbClient.table(tableName, TableSchema.fromBean(ItemEntity.class));
    }

    public List<Item> listItems(int limit) {
        logger.info("Listing items with limit: {}", limit);
        
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .limit(limit)
                .build();

        PageIterable<ItemEntity> results = table.scan(request);
        List<Item> items = new ArrayList<>();

        results.items().forEach(entity -> items.add(mapToModel(entity)));

        return items;
    }

    public Item getItem(String id) {
        logger.info("Getting item with ID: {}", id);
        
        ItemEntity entity = table.getItem(Key.builder().partitionValue(id).build());
        if (entity == null) {
            return null;
        }

        return mapToModel(entity);
    }

    public Item createItem(NewItem newItem) {
        logger.info("Creating new item: {}", newItem.getName());
        
        String now = OffsetDateTime.now().toString();
        
        ItemEntity entity = new ItemEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setName(newItem.getName());
        entity.setDescription(newItem.getDescription());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        table.putItem(entity);

        return mapToModel(entity);
    }

    public Item updateItem(String id, Item item) {
        logger.info("Updating item with ID: {}", id);
        
        // Check if item exists
        if (table.getItem(Key.builder().partitionValue(id).build()) == null) {
            return null;
        }

        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName(item.getName());
        entity.setDescription(item.getDescription());
        entity.setCreatedAt(item.getCreatedAt());
        entity.setUpdatedAt(OffsetDateTime.now().toString());

        table.putItem(entity);

        return mapToModel(entity);
    }

    public void deleteItem(String id) {
        logger.info("Deleting item with ID: {}", id);
        table.deleteItem(Key.builder().partitionValue(id).build());
    }

    private Item mapToModel(ItemEntity entity) {
        Item item = new Item();
        item.setId(entity.getId());
        item.setName(entity.getName());
        item.setDescription(entity.getDescription());
        item.setCreatedAt(entity.getCreatedAt());
        item.setUpdatedAt(entity.getUpdatedAt());
        return item;
    }
}
