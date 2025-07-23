package com.example.utils;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class HeaderUtils {
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String APPLICATION_JSON = "application/json";

    private HeaderUtils() {
        // Utility class, prevent instantiation
    }

    public static Map<String, String> getHeaders(APIGatewayProxyRequestEvent request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = request.getHeaders();
        return headers != null ? headers : Collections.emptyMap();
    }

    public static Optional<String> getHeader(APIGatewayProxyRequestEvent request, String headerName) {
        return Optional.ofNullable(getHeaders(request).get(headerName));
    }

    public static String getHeaderOrDefault(APIGatewayProxyRequestEvent request, String headerName, String defaultValue) {
        return getHeader(request, headerName).orElse(defaultValue);
    }
}
