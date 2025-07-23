package com.example.api.integration;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all integration tests.
 */
public abstract class BaseIntegrationTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    protected static String baseUrl;
    protected static String authToken;
    protected RequestSpecification requestSpec;
    
    @BeforeAll
    public static void setupClass() {
        // Get base URL from system property or environment variable
        baseUrl = System.getProperty("api.base.url");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = System.getenv("API_BASE_URL");
        }
        
        if (baseUrl == null || baseUrl.isEmpty()) {
            // Default to localhost for local testing
            baseUrl = "http://localhost:3000";
            logger.warn("No API base URL provided, using default: {}", baseUrl);
        }
        
        // For local testing, we can use a dummy token
        if (baseUrl.contains("localhost")) {
            authToken = "test-token";
            logger.info("Using dummy token for local testing");
        } else {
            // Get authentication token from Cognito
            authToken = getAuthToken();
        }
        
        logger.info("Using API base URL: {}", baseUrl);
    }
    
    @BeforeEach
    public void setup() {
        // Set up RestAssured
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
        
        if (authToken != null && !authToken.isEmpty()) {
            requestSpec.header("Authorization", "Bearer " + authToken);
        }
    }
    
    /**
     * Get authentication token from Cognito.
     * 
     * @return JWT token
     */
    private static String getAuthToken() {
        String userPoolId = System.getProperty("cognito.user.pool.id");
        String clientId = System.getProperty("cognito.client.id");
        String username = System.getProperty("test.username");
        String password = System.getProperty("test.password");
        
        if (userPoolId == null || clientId == null || username == null || password == null) {
            logger.warn("Cognito credentials not provided, using dummy token");
            return "test-token";
        }
        
        try {
            CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                    .region(Region.US_EAST_1)
                    .build();
            
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", username);
            authParams.put("PASSWORD", password);
            
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();
            
            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);
            return authResponse.authenticationResult().idToken();
        } catch (Exception e) {
            logger.error("Error getting authentication token", e);
            return "test-token";
        }
    }
}
