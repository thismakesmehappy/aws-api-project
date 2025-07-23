#!/bin/bash

# Exit on any error
set -e

# Default values
GITHUB_REPOSITORY=${GITHUB_REPOSITORY:-"thismakesmehappy/aws-api-project"}
GITHUB_SHA=${GITHUB_SHA:-$(git rev-parse HEAD)}

# Function to update GitHub commit status
update_github_status() {
    local state=$1
    local description=$2
    
    curl -H "Authorization: token $GITHUB_TOKEN" \
         -H "Accept: application/vnd.github.v3+json" \
         -X POST \
         "https://api.github.com/repos/$GITHUB_REPOSITORY/statuses/$GITHUB_SHA" \
         -d "{
           \"state\": \"$state\",
           \"target_url\": \"$CODEBUILD_BUILD_URL\",
           \"description\": \"$description\",
           \"context\": \"AWS Integration Tests\"
         }"
}

# Function to check test results
check_test_results() {
    local test_dir="integration-tests-java/target/surefire-reports"
    
    if [ ! -d "$test_dir" ]; then
        echo "No test results found in $test_dir"
        return 1
    fi
    
    if grep -r -l "<failure" "$test_dir" > /dev/null || \
       grep -r -l "<error" "$test_dir" > /dev/null; then
        return 1
    fi
    
    return 0
}

# Main execution
echo "Checking test results..."
if check_test_results; then
    echo "Tests passed successfully"
    update_github_status "success" "Integration tests passed in AWS"
    exit 0
else
    echo "Tests failed"
    update_github_status "failure" "Integration tests failed in AWS"
    exit 1
fi
