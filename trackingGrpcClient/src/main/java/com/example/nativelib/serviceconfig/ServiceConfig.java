package com.example.nativelib.serviceconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {
    public static Map<String, Object> createServiceConfig() {
        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", 10D);
        retryPolicy.put("initialBackoff", "10s");
        retryPolicy.put("maxBackoff", "20s");
        retryPolicy.put("backoffMultiplier", 2D);
        retryPolicy.put("retryableStatusCodes", Arrays.asList("UNAVAILABLE", "DEADLINE_EXCEEDED"));

        Map<String, Object> methodConfig = new HashMap<>();
        methodConfig.put("name", Collections.singletonList(
                Map.of("service", "org.dlms.services.UserService")
        ));
        methodConfig.put("retryPolicy", retryPolicy);
        methodConfig.put("waitForReady", true);

        return Map.of("methodConfig", Collections.singletonList(methodConfig));
    }
}
