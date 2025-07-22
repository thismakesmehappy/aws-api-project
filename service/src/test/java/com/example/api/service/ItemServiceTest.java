package com.example.api.service;

import com.example.api.data.ItemEntity;
import com.example.model.Item;
import com.example.model.NewItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    private DynamoDbEnhancedClient dynamoDbClient;
    private DynamoDbTable<ItemEntity> table;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        dynamoDbClient = mock(DynamoDbEnhancedClient.class);
        table = mock(DynamoDbTable.class);
        when(dynamoDbClient.table(anyString(), any())).thenReturn(table);
        itemService = new ItemService(dynamoDbClient, "test-table");
    }

    @Test
    void createItem_Success() {
        // Arrange
        NewItem newItem = new NewItem();
        newItem.setName("Test Item");
        newItem.setDescription("Test Description");

        ArgumentCaptor<ItemEntity> entityCaptor = ArgumentCaptor.forClass(ItemEntity.class);

        // Act
        Item result = itemService.createItem(newItem);

        // Assert
        verify(table).putItem(entityCaptor.capture());
        ItemEntity capturedEntity = entityCaptor.getValue();

        assertNotNull(result);
        assertEquals(newItem.getName(), result.getName());
        assertEquals(newItem.getDescription(), result.getDescription());
        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        assertEquals(newItem.getName(), capturedEntity.getName());
        assertEquals(newItem.getDescription(), capturedEntity.getDescription());
    }

    @Test
    void getItem_Success() {
        // Arrange
        String id = "test-id";
        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName("Test Item");
        entity.setDescription("Test Description");
        entity.setCreatedAt("2025-01-01T00:00:00Z");
        entity.setUpdatedAt("2025-01-01T00:00:00Z");

        when(table.getItem(any(Key.class))).thenReturn(entity);

        // Act
        Item result = itemService.getItem(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getDescription(), result.getDescription());
        assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void getItem_NotFound() {
        // Arrange
        String id = "non-existent-id";
        when(table.getItem(any(Key.class))).thenReturn(null);

        // Act
        Item result = itemService.getItem(id);

        // Assert
        assertNull(result);
    }

    @Test
    void updateItem_Success() {
        // Arrange
        String id = "test-id";
        Item item = new Item();
        item.setId(id);
        item.setName("Updated Item");
        item.setDescription("Updated Description");
        item.setCreatedAt("2025-01-01T00:00:00Z");

        ItemEntity existingEntity = new ItemEntity();
        existingEntity.setId(id);
        when(table.getItem(any(Key.class))).thenReturn(existingEntity);

        ArgumentCaptor<ItemEntity> entityCaptor = ArgumentCaptor.forClass(ItemEntity.class);

        // Act
        Item result = itemService.updateItem(id, item);

        // Assert
        verify(table).putItem(entityCaptor.capture());
        ItemEntity capturedEntity = entityCaptor.getValue();

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertNotNull(result.getUpdatedAt());

        assertEquals(id, capturedEntity.getId());
        assertEquals(item.getName(), capturedEntity.getName());
        assertEquals(item.getDescription(), capturedEntity.getDescription());
    }

    @Test
    void updateItem_NotFound() {
        // Arrange
        String id = "non-existent-id";
        Item item = new Item();
        item.setId(id);
        when(table.getItem(any(Key.class))).thenReturn(null);

        // Act
        Item result = itemService.updateItem(id, item);

        // Assert
        assertNull(result);
        verify(table, never()).putItem(any(ItemEntity.class));
    }

    @Test
    void deleteItem_Success() {
        // Arrange
        String id = "test-id";

        // Act
        itemService.deleteItem(id);

        // Assert
        verify(table).deleteItem(any(Key.class));
    }
}
