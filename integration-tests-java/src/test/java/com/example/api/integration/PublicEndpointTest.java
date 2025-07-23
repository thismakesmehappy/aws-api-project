package com.example.api.integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the public endpoint.
 */
public class PublicEndpointTest extends BaseIntegrationTest {
    
    @Test
    public void testGetPublicData() {
        given()
            .spec(requestSpec)
        .when()
            .get("/public")
        .then()
            .statusCode(200)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }
}
