package com.example.nativelib.serviceconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {
    public static Map<String, Object> createServiceConfig() {
        // Configuration for UserService
        Map<String, Object> methodConfig = new HashMap<>();
        methodConfig.put("name", Collections.singletonList(
                Map.of("service", "org.dlms.services.UserService")
        ));

        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", 10D);
        retryPolicy.put("initialBackoff", "10s");
        retryPolicy.put("maxBackoff", "20s");
        retryPolicy.put("backoffMultiplier", 2D);
        retryPolicy.put("retryableStatusCodes", Arrays.asList("UNAVAILABLE", "DEADLINE_EXCEEDED"));

        methodConfig.put("retryPolicy", retryPolicy);
        methodConfig.put("waitForReady", true);
        methodConfig.put("timeout", "5s");

        return Map.of("methodConfig", Collections.singletonList(methodConfig));
    }
}
