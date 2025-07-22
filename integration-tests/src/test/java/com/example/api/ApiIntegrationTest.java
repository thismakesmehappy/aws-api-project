package com.example.api;

import com.example.model.Item;
import com.example.model.NewItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ApiIntegrationTest {
    private static final String API_URL = System.getenv("API_URL");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @BeforeAll
    static void setUp() {
        assertNotNull(API_URL, "API_URL environment variable must be set");
    }

    @Test
    void createAndGetItem() throws Exception {
        // Create new item
        NewItem newItem = new NewItem();
        newItem.setName("Test Item");
        newItem.setDescription("Test Description");

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/items"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(newItem)))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        Item createdItem = objectMapper.readValue(createResponse.body(), Item.class);
        assertNotNull(createdItem.getId());
        assertEquals(newItem.getName(), createdItem.getName());
        assertEquals(newItem.getDescription(), createdItem.getDescription());

        // Get the created item
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/items/" + createdItem.getId()))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Item retrievedItem = objectMapper.readValue(getResponse.body(), Item.class);
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
        assertEquals(createdItem.getDescription(), retrievedItem.getDescription());
    }

    @Test
    void listItems() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/items"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Item[] items = objectMapper.readValue(response.body(), Item[].class);
        assertNotNull(items);
    }
}
