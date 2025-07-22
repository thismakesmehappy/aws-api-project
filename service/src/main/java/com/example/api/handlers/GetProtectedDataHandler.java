package com.example.api.handlers;

import com.example.api.repositories.DataRepository;
import com.example.api.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handler for the GetProtectedData operation.
 * This handler processes requests to the protected endpoint that requires authentication.
 */
public class GetProtectedDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(GetProtectedDataHandler.class);
    
    private final DataRepository dataRepository;
    private final AuthService authService;
    
    /**
     * Create a new GetProtectedDataHandler.
     */
    public GetProtectedDataHandler() {
        this.dataRepository = new DataRepository();
        this.authService = new AuthService();
    }
    
    /**
     * Handle a request to the protected endpoint.
     * 
     * @param userId The ID of the authenticated user
     * @return Map of protected data
     */
    public Map<String, String> handle(String userId) {
        logger.info("Processing protected data request for user: {}", userId);
        return dataRepository.getProtectedData(userId);
    }
}
