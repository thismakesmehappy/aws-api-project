package com.example.api.model;

/**
 * Represents a new item to be created.
 */
public class NewItem {
    private String name;
    private String description;
    
    public NewItem() {
    }
    
    public NewItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String name() {
        return name;
    }
    
    public String description() {
        return description;
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
}
