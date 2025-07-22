#!/bin/bash
# Helper script to get a Cognito token for API testing

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed. Please install jq first."
    echo "On macOS: brew install jq"
    echo "On Ubuntu: sudo apt-get install jq"
    exit 1
fi

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "Error: AWS CLI is required but not installed. Please install AWS CLI first."
    echo "See: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Default values
STACK_NAME="DevApiStack"
USERNAME=""
PASSWORD=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        --stack)
        STACK_NAME="$2"
        shift
        shift
        ;;
        --username)
        USERNAME="$2"
        shift
        shift
        ;;
        --password)
        PASSWORD="$2"
        shift
        shift
        ;;
        --help)
        echo "Usage: $0 [options]"
        echo "Options:"
        echo "  --stack STACK_NAME    CloudFormation stack name (default: DevApiStack)"
        echo "  --username USERNAME   Cognito username/email"
        echo "  --password PASSWORD   Cognito password"
        echo "  --help                Show this help message"
        exit 0
        ;;
        *)
        echo "Unknown option: $1"
        echo "Use --help for usage information"
        exit 1
        ;;
    esac
done

# Check if username and password are provided
if [ -z "$USERNAME" ] || [ -z "$PASSWORD" ]; then
    echo "Error: Username and password are required"
    echo "Usage: $0 --username your-email@example.com --password YourPassword123!"
    exit 1
fi

echo "Getting Cognito User Pool and Client IDs from stack $STACK_NAME..."

# Get User Pool ID and Client ID from CloudFormation outputs
USER_POOL_ID=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='UserPoolId'].OutputValue" --output text)
CLIENT_ID=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='UserPoolClientId'].OutputValue" --output text)

if [ -z "$USER_POOL_ID" ] || [ -z "$CLIENT_ID" ]; then
    echo "Error: Could not retrieve User Pool ID or Client ID from stack $STACK_NAME"
    exit 1
fi

echo "User Pool ID: $USER_POOL_ID"
echo "Client ID: $CLIENT_ID"

# Check if the user exists
USER_EXISTS=$(aws cognito-idp admin-get-user --user-pool-id $USER_POOL_ID --username $USERNAME 2>&1 || echo "NOT_FOUND")

if [[ $USER_EXISTS == *"UserNotFoundException"* ]] || [[ $USER_EXISTS == "NOT_FOUND" ]]; then
    echo "User $USERNAME does not exist. Creating new user..."
    
    # Sign up the user
    SIGN_UP_RESULT=$(aws cognito-idp sign-up \
        --client-id $CLIENT_ID \
        --username $USERNAME \
        --password $PASSWORD 2>&1)
    
    if [[ $SIGN_UP_RESULT == *"error"* ]]; then
        echo "Error signing up user: $SIGN_UP_RESULT"
        exit 1
    fi
    
    echo "User created. Confirming user..."
    
    # Confirm the user (admin confirmation without verification code)
    aws cognito-idp admin-confirm-sign-up \
        --user-pool-id $USER_POOL_ID \
        --username $USERNAME
    
    echo "User confirmed."
else
    echo "User $USERNAME already exists."
fi

echo "Getting authentication token..."

# Get authentication token
AUTH_RESULT=$(aws cognito-idp initiate-auth \
    --client-id $CLIENT_ID \
    --auth-flow USER_PASSWORD_AUTH \
    --auth-parameters USERNAME=$USERNAME,PASSWORD=$PASSWORD)

# Extract the ID token
ID_TOKEN=$(echo $AUTH_RESULT | jq -r '.AuthenticationResult.IdToken')

if [ -z "$ID_TOKEN" ] || [ "$ID_TOKEN" == "null" ]; then
    echo "Error: Could not retrieve token"
    echo $AUTH_RESULT
    exit 1
fi

echo "Token acquired successfully!"
echo ""
echo "To use this token with curl:"
echo "curl -H \"Authorization: Bearer $ID_TOKEN\" YOUR_API_URL/protected"
echo ""
echo "Token value:"
echo "$ID_TOKEN"

# Get API URL
API_URL=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='ApiUrl'].OutputValue" --output text)

if [ ! -z "$API_URL" ]; then
    echo ""
    echo "Your API URL is: $API_URL"
    echo ""
    echo "Example command to test protected endpoint:"
    echo "curl -H \"Authorization: Bearer $ID_TOKEN\" $API_URL/protected"
fi
