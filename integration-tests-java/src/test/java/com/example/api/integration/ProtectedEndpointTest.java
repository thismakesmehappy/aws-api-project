package com.example.api.integration;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the protected endpoint.
 */
public class ProtectedEndpointTest extends BaseIntegrationTest {
    
    @Test
    public void testGetProtectedData_WithAuth() {
        given()
            .spec(requestSpec)
        .when()
            .get("/protected")
        .then()
            .statusCode(200)
            .body("message", notNullValue())
            .body("userId", notNullValue())
            .body("timestamp", notNullValue());
    }
    
    @Test
    public void testGetProtectedData_WithoutAuth() {
        // Create a request spec without auth token
        RequestSpecification noAuthSpec = given()
            .baseUri(baseUrl)
            .contentType("application/json");
        
        given()
            .spec(noAuthSpec)
        .when()
            .get("/protected")
        .then()
            .statusCode(401)
            .body("code", equalTo("UNAUTHORIZED"));
    }
}
