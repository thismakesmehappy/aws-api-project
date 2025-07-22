# Testing Guide

## Overview

This project uses a comprehensive testing strategy including:
- Unit tests
- Integration tests
- End-to-end tests
- Load tests

## Test Structure

```
aws-api-project/
├── service/
│   └── src/
│       └── test/
│           └── java/
│               └── com/
│                   └── example/
│                       └── api/
│                           ├── service/
│                           │   └── ItemServiceTest.java
│                           └── ApiHandlerTest.java
├── integration-tests/
│   └── src/
│       └── test/
│           └── java/
│               └── com/
│                   └── example/
│                       └── api/
│                           └── ApiIntegrationTest.java
└── load-tests/
    └── k6/
        └── scenarios/
            └── basic-load.js
```

## Running Tests

### Unit Tests

```bash
# Run all unit tests
cd service
mvn test

# Run specific test class
mvn test -Dtest=ItemServiceTest

# Run specific test method
mvn test -Dtest=ItemServiceTest#createItem_Success
```

### Integration Tests

```bash
# Run all integration tests
cd integration-tests
npm test

# Run with specific API URL
API_URL=https://your-api.example.com npm test
```

### Load Tests

```bash
# Install k6
brew install k6

# Run load test
k6 run load-tests/k6/scenarios/basic-load.js
```

## Writing Tests

### Unit Tests

Example unit test:
```java
@Test
void createItem_Success() {
    // Arrange
    NewItem newItem = new NewItem();
    newItem.setName("Test Item");
    newItem.setDescription("Test Description");

    // Act
    Item result = itemService.createItem(newItem);

    // Assert
    assertNotNull(result);
    assertEquals(newItem.getName(), result.getName());
    assertEquals(newItem.getDescription(), result.getDescription());
}
```

### Integration Tests

Example integration test:
```java
@Test
void createAndGetItem() throws Exception {
    // Create new item
    NewItem newItem = new NewItem();
    newItem.setName("Test Item");
    newItem.setDescription("Test Description");

    HttpResponse<String> createResponse = // ... create item

    // Verify creation
    assertEquals(201, createResponse.statusCode());

    // Get the created item
    HttpResponse<String> getResponse = // ... get item

    // Verify retrieval
    assertEquals(200, getResponse.statusCode());
}
```

## Test Data

### Test Data Management

1. Use meaningful test data:
```java
NewItem item = new NewItem();
item.setName("Test Product");
item.setDescription("High-quality test product");
```

2. Clean up test data:
```java
@AfterEach
void cleanup() {
    // Delete test items
}
```

### Test Fixtures

Create reusable test fixtures:
```java
public class TestFixtures {
    public static NewItem createTestItem() {
        NewItem item = new NewItem();
        item.setName("Test Item");
        item.setDescription("Test Description");
        return item;
    }
}
```

## Mocking

### Using Mockito

```java
@Test
void testWithMocks() {
    // Create mock
    DynamoDbTable<ItemEntity> tableMock = mock(DynamoDbTable.class);
    
    // Set up behavior
    when(tableMock.getItem(any(Key.class)))
        .thenReturn(createTestEntity());
    
    // Verify interactions
    verify(tableMock).putItem(any(ItemEntity.class));
}
```

### Mock AWS Services

```java
public class TestConfig {
    public static DynamoDbClient createMockDynamoDbClient() {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:8000"))
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("dummy", "dummy")))
            .build();
    }
}
```

## Test Coverage

### JaCoCo Coverage Reports

```bash
# Generate coverage report
mvn clean verify

# View report
open target/site/jacoco/index.html
```

### Coverage Thresholds

Minimum coverage requirements:
- Line coverage: 80%
- Branch coverage: 70%
- Method coverage: 90%

## Continuous Integration

### GitHub Actions

Tests run automatically on:
- Pull requests
- Pushes to main branch
- Manual workflow dispatch

### Test Reports

Access test reports:
1. Go to GitHub Actions
2. Select workflow run
3. Download artifacts
4. View test results

## Best Practices

### General Guidelines

1. Follow AAA pattern:
   - Arrange
   - Act
   - Assert

2. One assertion per test:
```java
@Test
void itemName_ShouldNotBeEmpty() {
    NewItem item = new NewItem();
    assertThrows(ValidationException.class, () -> 
        itemService.createItem(item));
}
```

3. Use meaningful names:
```java
@Test
void createItem_WithValidData_ShouldSucceed()
```

### Testing Antipatterns

Avoid:
- Testing implementation details
- Brittle tests
- Test interdependence
- Complex test setup

## Troubleshooting

### Common Issues

1. Tests fail in CI but pass locally:
   - Check environment differences
   - Verify AWS credentials
   - Check for race conditions

2. Flaky tests:
   - Add logging
   - Check async operations
   - Verify cleanup

3. Slow tests:
   - Use test containers
   - Implement parallel execution
   - Mock external services

### Debug Logging

Enable debug logging:
```bash
mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

## Performance Testing

### K6 Load Tests

```javascript
import http from 'k6/http';
import { check } from 'k6';

export default function() {
    const res = http.get('https://api.example.com/items');
    check(res, {
        'is status 200': (r) => r.status === 200
    });
}
```

### Performance Metrics

Monitor:
- Response time
- Error rate
- Throughput
- Resource utilization
