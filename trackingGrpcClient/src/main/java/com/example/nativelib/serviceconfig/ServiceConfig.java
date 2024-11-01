package com.example.nativelib.serviceconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {
    public static Map<String, Object> createServiceConfig() {
        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", "5");
        retryPolicy.put("initialBackoff", "0.1s");
        retryPolicy.put("maxBackoff", "1s");
        retryPolicy.put("backoffMultiplier", 2.0);
        retryPolicy.put("retryableStatusCodes", Arrays.asList("UNAVAILABLE", "DEADLINE_EXCEEDED"));
        Map<String, Object> methodConfig = new HashMap<>();
        methodConfig.put("name", Collections.singletonList(Map.of(
                "service", "org.dlms.services.EventTrackingService",
                "method", "trackImpression"
        )));
        methodConfig.put("retryPolicy", retryPolicy);
        return Map.of("methodConfig", Collections.singletonList(methodConfig));
    }
}
