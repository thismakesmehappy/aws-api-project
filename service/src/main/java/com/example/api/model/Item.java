package com.example.api.model;

import java.time.OffsetDateTime;

/**
 * Represents an item in the system.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    public Item() {
    }
    
    public Item(String id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String id() {
        return id;
    }
    
    public String name() {
        return name;
    }
    
    public String description() {
        return description;
    }
    
    public OffsetDateTime createdAt() {
        return createdAt;
    }
    
    public OffsetDateTime updatedAt() {
        return updatedAt;
    }
    
    public Item withUpdatedAt(OffsetDateTime updatedAt) {
        return new Item(this.id, this.name, this.description, this.createdAt, updatedAt);
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
