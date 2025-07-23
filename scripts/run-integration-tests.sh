#!/bin/bash

# Start the local server
echo "Starting local server..."
cd "$(dirname "$0")/../local-dev"
node server.js &
SERVER_PID=$!

# Wait for server to start
echo "Waiting for server to start..."
sleep 3

# Run Java integration tests
echo "Running Java integration tests..."
cd ../integration-tests-java
mvn test -Dapi.base.url=http://localhost:3000
JAVA_EXIT_CODE=$?

# Stop the server
echo "Stopping local server..."
kill $SERVER_PID

# Determine exit code
if [ $JAVA_EXIT_CODE -ne 0 ]; then
  echo "Integration tests failed!"
  exit 1
else
  echo "All integration tests passed!"
  exit 0
fi
