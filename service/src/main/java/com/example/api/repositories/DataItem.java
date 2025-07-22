package com.example.api.repositories;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an item stored in DynamoDB.
 */
@DynamoDbBean
public class DataItem {
    private String pk;
    private String sk;
    private String id;
    private String name;
    private String description;
    private Map<String, String> attributes;
    private String ownerId;
    private boolean isPublic;
    private Instant createdAt;
    private Instant updatedAt;
    
    /**
     * Default constructor required by DynamoDB Enhanced Client.
     */
    public DataItem() {
        this.attributes = new HashMap<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Create a new DataItem.
     * 
     * @param id The unique identifier for the item
     * @param name The name of the item
     * @param ownerId The ID of the user who owns the item
     * @param isPublic Whether the item is publicly accessible
     */
    public DataItem(String id, String name, String ownerId, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.isPublic = isPublic;
        this.pk = "ITEM#" + id;
        this.sk = "METADATA";
        this.attributes = new HashMap<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }
    
    public void setPk(String pk) {
        this.pk = pk;
    }
    
    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }
    
    public void setSk(String sk) {
        this.sk = sk;
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
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Add an attribute to the item.
     * 
     * @param key The attribute key
     * @param value The attribute value
     * @return This item for chaining
     */
    public DataItem addAttribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataItem dataItem = (DataItem) o;
        return isPublic == dataItem.isPublic &&
               Objects.equals(pk, dataItem.pk) &&
               Objects.equals(sk, dataItem.sk) &&
               Objects.equals(id, dataItem.id) &&
               Objects.equals(name, dataItem.name) &&
               Objects.equals(description, dataItem.description) &&
               Objects.equals(attributes, dataItem.attributes) &&
               Objects.equals(ownerId, dataItem.ownerId) &&
               Objects.equals(createdAt, dataItem.createdAt) &&
               Objects.equals(updatedAt, dataItem.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pk, sk, id, name, description, attributes, ownerId, isPublic, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "DataItem{" +
               "pk='" + pk + '\'' +
               ", sk='" + sk + '\'' +
               ", id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", attributes=" + attributes +
               ", ownerId='" + ownerId + '\'' +
               ", isPublic=" + isPublic +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
