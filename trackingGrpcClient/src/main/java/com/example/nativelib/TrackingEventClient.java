package com.example.nativelib;

import static java.util.logging.Level.ALL;

import android.util.Log;

import org.dlms.services.ActionEvent;
import org.dlms.services.EventTrackingServiceGrpc;
import org.dlms.services.ImpressionEvent;

import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TrackingEventClient {
    private static final int PORT = 50052;
    private StreamObserver<ActionEvent> actionEventStreamObserver;
    private StreamObserver<ImpressionEvent> impressionEventStreamObserver;

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
        // Set gRPC log level
        Logger.getLogger("io.grpc").setLevel(ALL);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("192.168.1.10", PORT)
                .usePlaintext()
                .build();

        EventTrackingServiceGrpc.EventTrackingServiceStub stub =
                EventTrackingServiceGrpc
                        .newStub(channel);

        actionEventStreamObserver = stub.trackAction(new EventResponseObserver());
        impressionEventStreamObserver = stub.trackImpression(new EventResponseObserver());
    }

    public void trackActionEvent(ActionEvent event) {
        Log.d("TrackingEventClient", "trackActionEvent called");
        actionEventStreamObserver.onNext(event);
    }

    public void trackImpressionEvent(ImpressionEvent event) {
        Log.d("TrackingEventClient", "trackImpressionEvent called");
        impressionEventStreamObserver.onNext(event);
    }
}
