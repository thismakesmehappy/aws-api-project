# API Integration Tests

This module contains integration tests for the API service using REST Assured and JUnit 5.

## Overview

These tests verify that the API endpoints work correctly when deployed to different environments. They test:

- Public endpoint accessibility
- Protected endpoint authentication
- CRUD operations on items
- Error handling

## Running the Tests

### Local Testing

To run the tests against a local server:

```bash
mvn test -Dapi.base.url=http://localhost:3000
```

### Testing Against Deployed Environments

To run the tests against a deployed environment:

```bash
mvn test -Dapi.base.url=https://your-api-url.execute-api.us-east-1.amazonaws.com/dev
```

### Authentication

For local testing, a dummy token is used. For testing against deployed environments with Cognito authentication, you can provide Cognito credentials:

```bash
mvn test \
  -Dapi.base.url=https://your-api-url.execute-api.us-east-1.amazonaws.com/dev \
  -Dcognito.user.pool.id=us-east-1_xxxxxxxx \
  -Dcognito.client.id=xxxxxxxxxxxxxxxxxxxxxxxxxx \
  -Dtest.username=test-user@example.com \
  -Dtest.password=Test-Password-123
```

## Test Structure

- `BaseIntegrationTest.java` - Base class for all tests, handles authentication and common setup
- `PublicEndpointTest.java` - Tests for the public endpoint
- `ProtectedEndpointTest.java` - Tests for the protected endpoint
- `ItemsEndpointTest.java` - Tests for the items endpoints (CRUD operations)

## Model Classes

- `Item.java` - Model class for item data
- `ErrorResponse.java` - Model class for error responses

## Configuration

The tests use the following configuration:

- REST Assured for API testing
- JUnit 5 for test execution
- SLF4J with Simple Logger for logging
- Jackson for JSON serialization/deserialization
- AWS SDK for Cognito authentication (when testing against deployed environments)

## Adding New Tests

To add tests for a new endpoint:

1. Create a new test class that extends `BaseIntegrationTest`
2. Add test methods using REST Assured to verify the endpoint behavior
3. Use appropriate assertions to validate responses

Example:

```java
public class NewEndpointTest extends BaseIntegrationTest {
    
    @Test
    public void testNewEndpoint() {
        given()
            .spec(requestSpec)
        .when()
            .get("/new-endpoint")
        .then()
            .statusCode(200)
            .body("key", equalTo("value"));
    }
}
```

## CI/CD Integration

These tests are integrated into the CI/CD pipeline and run:
- After building the service locally
- After deploying to the dev environment
- After deploying to staging (with rollback capability)
- After deploying to production (with rollback capability)
