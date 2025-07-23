#!/bin/bash

# Get the absolute path to the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Function to cleanup resources
cleanup() {
    local pids=$(lsof -t -i:3000)
    if [ ! -z "$pids" ]; then
        echo "Cleaning up server process(es)..."
        kill -9 $pids || true
    fi
    if [ -f "$PROJECT_ROOT/local-dev/server.log" ]; then
        echo "Server logs:"
        cat "$PROJECT_ROOT/local-dev/server.log"
        rm "$PROJECT_ROOT/local-dev/server.log"
    fi
}

# Ensure cleanup runs on script exit
trap cleanup EXIT

# Ensure no existing server is running
cleanup

# Start the server with logging
echo "Starting server..."
cd "$PROJECT_ROOT/local-dev" || exit 1
node server.js > server.log 2>&1 &
SERVER_PID=$!

# Wait briefly to catch immediate startup failures
sleep 1
if ! ps -p $SERVER_PID > /dev/null; then
    echo "Server failed to start!"
    echo "Server logs:"
    cat server.log
    exit 1
fi

# Wait for server to be ready
"$SCRIPT_DIR/wait-for-server.sh" $SERVER_PID
if [ $? -ne 0 ]; then
    echo "Server failed to start properly"
    exit 1
fi

# Run the tests with retries
echo "Running integration tests..."
cd "$PROJECT_ROOT/integration-tests-java" || exit 1

MAX_TEST_RETRIES=3
for i in $(seq 1 $MAX_TEST_RETRIES); do
    echo "Test attempt $i of $MAX_TEST_RETRIES"
    
    # Check if server is still running before each test attempt
    if ! ps -p $SERVER_PID > /dev/null; then
        echo "Server process died during testing!"
        echo "Server logs:"
        cat "$PROJECT_ROOT/local-dev/server.log"
        exit 1
    fi
    
    # Run the tests
    mvn test -Dapi.base.url=http://localhost:3000
    TEST_EXIT_CODE=$?
    
    if [ $TEST_EXIT_CODE -eq 0 ]; then
        echo "Tests passed successfully!"
        exit 0
    fi
    
    if [ $i -lt $MAX_TEST_RETRIES ]; then
        echo "Tests failed, waiting 30 seconds before retry..."
        sleep 30
    fi
done

echo "Tests failed after $MAX_TEST_RETRIES attempts"
exit 1
