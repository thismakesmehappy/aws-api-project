package com.example.api.integration;

import com.example.api.integration.model.Item;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the items endpoints.
 * Tests are ordered to ensure proper sequence of CRUD operations.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemsEndpointTest extends BaseIntegrationTest {
    
    private static String createdItemId;
    
    @Test
    @Order(1)
    public void testListItems() {
        given()
            .spec(requestSpec)
        .when()
            .get("/items")
        .then()
            .statusCode(200)
            .body("", instanceOf(java.util.List.class));
    }
    
    @Test
    @Order(2)
    public void testCreateItem() {
        Item newItem = new Item("Test Item", "This is a test item created by integration tests");
        
        Response response = given()
            .spec(requestSpec)
            .body(newItem)
        .when()
            .post("/items")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo(newItem.getName()))
            .body("description", equalTo(newItem.getDescription()))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue())
            .extract().response();
        
        // Store the created item ID for later tests
        createdItemId = response.jsonPath().getString("id");
        logger.info("Created item with ID: {}", createdItemId);
    }
    
    @Test
    @Order(3)
    public void testGetItem() {
        // Skip if no item was created
        if (createdItemId == null) {
            logger.warn("Skipping testGetItem because no item was created");
            return;
        }
        
        given()
            .spec(requestSpec)
        .when()
            .get("/items/" + createdItemId)
        .then()
            .statusCode(200)
            .body("id", equalTo(createdItemId))
            .body("name", notNullValue())
            .body("description", notNullValue())
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }
    
    @Test
    @Order(4)
    public void testUpdateItem() {
        // Skip if no item was created
        if (createdItemId == null) {
            logger.warn("Skipping testUpdateItem because no item was created");
            return;
        }
        
        Item updatedItem = new Item("Updated Test Item", "This item was updated by integration tests");
        
        given()
            .spec(requestSpec)
            .body(updatedItem)
        .when()
            .put("/items/" + createdItemId)
        .then()
            .statusCode(200)
            .body("id", equalTo(createdItemId))
            .body("name", equalTo(updatedItem.getName()))
            .body("description", equalTo(updatedItem.getDescription()))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }
    
    @Test
    @Order(5)
    public void testDeleteItem() {
        // Skip if no item was created
        if (createdItemId == null) {
            logger.warn("Skipping testDeleteItem because no item was created");
            return;
        }
        
        given()
            .spec(requestSpec)
        .when()
            .delete("/items/" + createdItemId)
        .then()
            .statusCode(204);
        
        // Verify the item was deleted
        given()
            .spec(requestSpec)
        .when()
            .get("/items/" + createdItemId)
        .then()
            .statusCode(404)
            .body("code", equalTo("NOT_FOUND"));
    }
    
    @Test
    @Order(6)
    public void testGetNonExistentItem() {
        given()
            .spec(requestSpec)
        .when()
            .get("/items/non-existent-id")
        .then()
            .statusCode(404)
            .body("code", equalTo("NOT_FOUND"));
    }
    
    @Test
    @Order(7)
    public void testCreateItemWithInvalidData() {
        // Missing required name field
        Item invalidItem = new Item(null, "This item has no name");
        
        given()
            .spec(requestSpec)
            .body(invalidItem)
        .when()
            .post("/items")
        .then()
            .statusCode(400)
            .body("code", equalTo("BAD_REQUEST"));
    }
}
