# Local Development Guide

This guide explains how to set up and use the local development environment for testing the AWS API project without deploying to AWS.

## Prerequisites

- Docker and Docker Compose
- Java 17 or later
- Maven
- AWS CLI
- jq (JSON processor)

## Installation Instructions

### macOS
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install required tools
brew install docker docker-compose awscli jq

# Install Java 17
brew install openjdk@17

# Create symlink for Java 17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

### Ubuntu/Debian
```bash
# Update package list
sudo apt update

# Install Docker and Docker Compose
sudo apt install docker.io docker-compose

# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Install jq
sudo apt install jq

# Install Java 17
sudo apt install openjdk-17-jdk
```

### Windows
1. Install Docker Desktop from https://www.docker.com/products/docker-desktop
2. Install AWS CLI from https://aws.amazon.com/cli/
3. Install jq from https://stedolan.github.io/jq/download/
4. Install Java 17 from https://adoptium.net/

## Maven Toolchains Setup

Create a `~/.m2/toolchains.xml` file with the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 http://maven.apache.org/xsd/toolchains-1.1.0.xsd">
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>17</version>
      <vendor>openjdk</vendor>
    </provides>
    <configuration>
      <jdkHome>/path/to/your/jdk17</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

Replace `/path/to/your/jdk17` with the actual path to your Java 17 installation:
- macOS: `/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home`
- Linux: `/usr/lib/jvm/java-17-openjdk-amd64`
- Windows: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot`

## Project Structure

```
aws-api-project/
├── docker-compose.yml          # LocalStack configuration
├── scripts/
│   ├── local-dev.sh           # Script to start local environment
│   └── test-local.sh          # Script to run API tests locally
└── ...
```

## Starting the Local Environment

1. Navigate to the project directory:
```bash
cd aws-api-project
```

2. Start the local environment:
```bash
./scripts/local-dev.sh
```

This script will:
- Build the model and service projects
- Start LocalStack
- Create a local DynamoDB table
- Deploy the Lambda function
- Set up API Gateway endpoints

## Testing Locally

1. In a new terminal, run the test script:
```bash
./scripts/test-local.sh
```

This will run through a series of API tests:
- GET /items (list items)
- POST /items (create item)
- GET /items/{id} (get specific item)
- PUT /items/{id} (update item)
- DELETE /items/{id} (delete item)

## Available Endpoints

The local API will be available at:
```
http://localhost:4566/restapis/{api-id}/local/_user_request_/
```

Available endpoints:

### Public Endpoints
- `GET /public` - Get public data (no authentication required)

### Protected Endpoints (require authentication)
- `GET /protected` - Get protected data

### Item Management Endpoints (require authentication)
- `GET /items` - List all items (supports optional `limit` query parameter)
- `POST /items` - Create a new item
- `GET /items/{itemId}` - Get a specific item by ID
- `PUT /items/{itemId}` - Update an existing item
- `DELETE /items/{itemId}` - Delete an item

For detailed information about request/response formats and example usage, see [API Endpoints Documentation](API_ENDPOINTS.md).

## Making Changes

1. Modify the code in the service or model projects

2. Rebuild the service:
```bash
cd service
mvn clean package
```

3. Update the Lambda function:
```bash
aws lambda update-function-code \
    --endpoint-url http://localhost:4566 \
    --function-name api-handler \
    --zip-file fileb://target/api-service-1.0-SNAPSHOT.jar
```

## Viewing Logs

To view LocalStack logs:
```bash
docker-compose logs -f
```

## Cleaning Up

To stop and remove all local resources:
```bash
docker-compose down
```

## Troubleshooting

1. **Docker not running**
   - Start Docker Desktop (Windows/macOS)
   - Run `sudo service docker start` (Linux)

2. **Port conflicts**
   - Check if ports 4566 and 4571 are available
   - Stop any other LocalStack instances

3. **Build failures**
   - Ensure Java 17 and Maven are installed
   - Check Maven dependencies
   - Verify Maven toolchains configuration

4. **API not responding**
   - Verify LocalStack is running: `docker ps`
   - Check LocalStack logs: `docker-compose logs`
   - Ensure Lambda function is deployed correctly

5. **Java version issues**
   - Run `java -version` to check your default Java version
   - Verify Maven toolchains configuration
   - Make sure Java 17 is installed correctly

## Environment Variables

The local environment uses these default values:
- AWS_ACCESS_KEY_ID=test
- AWS_SECRET_ACCESS_KEY=test
- AWS_DEFAULT_REGION=us-east-1
- TABLE_NAME=items-table-local

> **Note**: The application is hardcoded to use the `us-east-1` region, so the local environment should also use this region.
