package com.example.nativelib;

import com.example.nativelib.serviceconfig.ServiceConfig;

import org.dlms.services.ActionEvent;
import org.dlms.services.EventTrackingServiceGrpc;
import org.dlms.services.ImpressionEvent;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TrackingEventClient {
    private static final int PORT = 50052;
    private EventTrackingServiceGrpc.EventTrackingServiceStub asyncStub;
    private StreamObserver<ActionEvent> actionEventStream;
    private StreamObserver<ImpressionEvent> impressionEventStream;

    private static TrackingEventClient instance;
    public static TrackingEventClient init() {
        if (instance == null) {
            instance = new TrackingEventClient();
            instance.initialize();
        }
        return instance;
    }

    public static TrackingEventClient getInstance() {
        return init();
    }

    private void initialize() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("192.168.1.10", PORT)
                .usePlaintext()
                .defaultServiceConfig(ServiceConfig.createServiceConfig())
                .enableRetry()
                .build();

        asyncStub = EventTrackingServiceGrpc.newStub(channel);
        createStreams();
    }

    private void createStreams() {
        impressionEventStream = createActionEventStream();
        actionEventStream = createImpressionEventStream();
    }

    int attempt = 1;
    private void reconnectStreamWithBackoff() {
        attempt++;
        int maxAttempts = 5;
        while (attempt <= maxAttempts) {
            try {
                long backoffMillis = (long) Math.pow(2, attempt) * 200;
                System.out.printf("Retrying connection in %d ms (attempt %d)%n", backoffMillis, attempt);
                Thread.sleep(1500);
                createStreams();  // Re-establish the stream
                return;  // Exit after successful reconnection
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attempt++;
        }
        System.err.println("Failed to reconnect after " + maxAttempts + " attempts.");
    }

    private StreamObserver<ActionEvent> createImpressionEventStream() {
        return asyncStub.trackAction(new EventResponseObserver("ACTION", this::reconnectStreamWithBackoff));
    }

    private StreamObserver<ImpressionEvent> createActionEventStream() {
        return asyncStub.trackImpression(new EventResponseObserver("IMPRESSION", this::reconnectStreamWithBackoff));
    }

    public StreamObserver<ActionEvent> getActionEventStream() {
        return actionEventStream;
    }

    public StreamObserver<ImpressionEvent> getImpressionEventStream() {
        return impressionEventStream;
    }
}
