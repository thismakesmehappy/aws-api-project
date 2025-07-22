package com.example.api.handlers;

import com.example.api.repositories.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Handler for the GetPublicData operation.
 * This handler processes requests to the public endpoint that doesn't require authentication.
 */
public class GetPublicDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(GetPublicDataHandler.class);
    
    private final DataRepository dataRepository;
    
    /**
     * Create a new GetPublicDataHandler.
     */
    public GetPublicDataHandler() {
        this.dataRepository = new DataRepository();
    }
    
    /**
     * Handle a request to the public endpoint.
     * 
     * @return Map of public data
     */
    public Map<String, String> handle() {
        logger.info("Processing public data request");
        return dataRepository.getPublicData();
    }
}
