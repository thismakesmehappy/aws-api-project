package com.example.api.model;

/**
 * Represents an API error response.
 */
public class Error {
    private String code;
    private String message;
    
    public Error() {
    }
    
    public Error(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
