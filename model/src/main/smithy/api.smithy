// Main API definition file
namespace com.example.api

use aws.protocols#restJson1
use aws.auth#cognitoUserPools

// Define the main service
@restJson1
service ApiService {
    version: "1.0.0",
    operations: [
        GetPublicData,
        GetProtectedData
    ]
}

// Public operation - no authentication required
@http(method: "GET", uri: "/public")
operation GetPublicData {
    output: GetPublicDataOutput
}

structure GetPublicDataOutput {
    @required
    message: String,
    
    @required
    timestamp: Timestamp
}

// Protected operation - requires authentication
@auth([
    {
        type: cognitoUserPools
    }
])
@http(method: "GET", uri: "/protected")
operation GetProtectedData {
    output: GetProtectedDataOutput
}

structure GetProtectedDataOutput {
    @required
    message: String,
    
    @required
    userId: String,
    
    @required
    timestamp: Timestamp
}
