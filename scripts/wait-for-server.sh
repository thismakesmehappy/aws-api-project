#!/bin/bash

SERVER_PID=$1
MAX_RETRIES=30
RETRY_INTERVAL=2

if [ -z "$SERVER_PID" ]; then
    echo "Usage: $0 <server-pid>"
    exit 1
fi

echo "Waiting for server to be ready..."
for i in $(seq 1 $MAX_RETRIES); do
    # Check if server process is still running
    if ! ps -p $SERVER_PID > /dev/null; then
        echo "Server process died while waiting for it to be ready"
        exit 1
    fi
    
    # Try to connect to the server
    if curl -s http://localhost:3000/api-docs > /dev/null; then
        echo "Server is ready!"
        exit 0
    fi
    
    echo "Attempt $i of $MAX_RETRIES - Server not ready yet..."
    sleep $RETRY_INTERVAL
done

echo "Server failed to become ready within $(($MAX_RETRIES * $RETRY_INTERVAL)) seconds"
exit 1
