package com.example.api.service;

import com.example.api.data.ItemEntity;
import com.example.api.model.Item;
import com.example.api.model.NewItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    private DynamoDbEnhancedClient dynamoDbClient;
    private DynamoDbTable<ItemEntity> table;
    private ItemService itemService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        dynamoDbClient = mock(DynamoDbEnhancedClient.class);
        table = mock(DynamoDbTable.class);
        when(dynamoDbClient.table(anyString(), any(TableSchema.class))).thenReturn(table);
        itemService = new ItemService(dynamoDbClient, "test-table");
    }

    @Test
    void createItem_Success() {
        // Arrange
        NewItem newItem = new NewItem("Test Item", "Test Description");

        ArgumentCaptor<ItemEntity> entityCaptor = ArgumentCaptor.forClass(ItemEntity.class);

        // Act
        Item result = itemService.createItem(newItem);

        // Assert
        verify(table).putItem(entityCaptor.capture());
        ItemEntity capturedEntity = entityCaptor.getValue();

        assertNotNull(result);
        assertEquals(newItem.name(), result.name());
        assertEquals(newItem.description(), result.description());
        assertNotNull(result.id());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        assertEquals(newItem.name(), capturedEntity.getName());
        assertEquals(newItem.description(), capturedEntity.getDescription());
    }

    @Test
    void getItem_Success() {
        // Arrange
        String id = "test-id";
        OffsetDateTime now = OffsetDateTime.now();
        
        ItemEntity entity = new ItemEntity();
        entity.setId(id);
        entity.setName("Test Item");
        entity.setDescription("Test Description");
        entity.setCreatedAt(now.format(DATE_FORMATTER));
        entity.setUpdatedAt(now.format(DATE_FORMATTER));

        when(table.getItem(any(Key.class))).thenReturn(entity);

        // Act
        Item result = itemService.getItem(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(entity.getName(), result.name());
        assertEquals(entity.getDescription(), result.description());
        assertEquals(now.format(DATE_FORMATTER), result.createdAt().format(DATE_FORMATTER));
        assertEquals(now.format(DATE_FORMATTER), result.updatedAt().format(DATE_FORMATTER));
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
        OffsetDateTime now = OffsetDateTime.now();
        
        Item item = new Item(
            id,
            "Updated Item",
            "Updated Description",
            now,
            now
        );

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
        assertEquals(id, result.id());
        assertEquals(item.name(), result.name());
        assertEquals(item.description(), result.description());
        assertNotNull(result.updatedAt());

        assertEquals(id, capturedEntity.getId());
        assertEquals(item.name(), capturedEntity.getName());
        assertEquals(item.description(), capturedEntity.getDescription());
    }

    @Test
    void updateItem_NotFound() {
        // Arrange
        String id = "non-existent-id";
        OffsetDateTime now = OffsetDateTime.now();
        
        Item item = new Item(
            id,
            "Updated Item",
            "Updated Description",
            now,
            now
        );
        
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
