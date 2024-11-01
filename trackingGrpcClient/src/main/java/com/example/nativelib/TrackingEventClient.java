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
                .enableRetry()
                .defaultServiceConfig(ServiceConfig.createServiceConfig())
                .build();

        EventTrackingServiceGrpc.EventTrackingServiceStub stub = EventTrackingServiceGrpc.newStub(channel);
        startStreaming(stub);
    }

    private void startStreaming(EventTrackingServiceGrpc.EventTrackingServiceStub stub) {
        impressionEventStream = createActionEventStream(stub);
        actionEventStream = createImpressionEventStream(stub);
    }

    private StreamObserver<ActionEvent> createImpressionEventStream(EventTrackingServiceGrpc.EventTrackingServiceStub stub) {
        return stub.trackAction(new EventResponseObserver("ACTION"));
    }

    private StreamObserver<ImpressionEvent> createActionEventStream(EventTrackingServiceGrpc.EventTrackingServiceStub stub) {
        return stub.trackImpression(new EventResponseObserver("IMPRESSION"));
    }

    public StreamObserver<ActionEvent> getActionEventStream() {
        return actionEventStream;
    }

    public StreamObserver<ImpressionEvent> getImpressionEventStream() {
        return impressionEventStream;
    }
}
