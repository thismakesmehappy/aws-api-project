# API Model Definitions

This directory contains the Smithy model definitions for our API.

## What is Smithy?

Smithy is a language for defining services and SDKs. It provides a clean, concise way to define your API contract, which can then be used to generate code, documentation, and more.

## Benefits of Using Smithy

1. **Strong Typing**: Define your API with a strongly-typed interface
2. **Code Generation**: Generate client and server code from your model
3. **Documentation**: Automatically generate API documentation
4. **Validation**: Validate requests and responses against your model
5. **Versioning**: Track changes to your API over time

## Project Structure

```
model/
├── src/
│   └── main/
│       └── smithy/
│           ├── api.smithy         # Main API definition
│           ├── operations/        # API operations
│           └── shapes/            # Data shapes/models
└── test/                          # Model tests
```

## Getting Started

1. Install the Smithy CLI:
   ```bash
   npm install -g smithy-cli
   ```

2. Validate your Smithy model:
   ```bash
   smithy validate
   ```

3. Generate code from your model:
   ```bash
   smithy build
   ```

## Example Smithy Model

```smithy
namespace com.example.api

use aws.protocols#restJson1

/// API for managing items
@restJson1
service ItemService {
    version: "1.0.0",
    operations: [GetItem, ListItems, CreateItem]
}

/// Get a single item by ID
@http(method: "GET", uri: "/items/{itemId}")
operation GetItem {
    input: GetItemInput,
    output: GetItemOutput
}

structure GetItemInput {
    /// The ID of the item to retrieve
    @required
    @httpLabel
    itemId: String
}

structure GetItemOutput {
    /// The retrieved item
    @required
    item: Item
}

/// An item in the system
structure Item {
    /// Unique identifier for the item
    @required
    id: String,
    
    /// Name of the item
    @required
    name: String,
    
    /// Description of the item
    description: String,
    
    /// Creation timestamp
    @required
    createdAt: Timestamp
}
```

## Authentication in Models

Smithy allows you to define authentication requirements in your models:

```smithy
@auth([
    {
        type: "cognitoUserPools",
        placement: "header",
        name: "Authorization"
    }
])
operation CreateItem {
    input: CreateItemInput,
    output: CreateItemOutput
}
```

This ensures that your API implementation enforces the authentication requirements defined in your model.

## Testing Models

You can write tests for your Smithy models to ensure they behave as expected:

```typescript
// Example test for validating a Smithy model
test('Item model should require id and name', () => {
  const validator = new SmithyValidator();
  const result = validator.validate('Item', {
    description: 'Test item'
  });
  
  expect(result.valid).toBe(false);
  expect(result.errors).toContain('Missing required field: id');
  expect(result.errors).toContain('Missing required field: name');
});
```
