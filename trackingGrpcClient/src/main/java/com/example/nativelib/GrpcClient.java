package com.example.nativelib;

import com.example.nativelib.interceptors.LoggingClientInterceptor;
import com.example.nativelib.monitor.GrpcChannelMonitor;
import com.example.nativelib.serviceconfig.ServiceConfig;
import com.example.nativelib.streamobservers.ChatStreamObserver;
import com.example.nativelib.streamobservers.EventResponseObserver;

import org.dlms.services.ActionEvent;
import org.dlms.services.ChatMessage;
import org.dlms.services.ChatServiceGrpc;
import org.dlms.services.EventTrackingServiceGrpc;
import org.dlms.services.ImpressionEvent;
import org.dlms.services.LiveScoreServiceGrpc;
import org.dlms.services.ScoreRequest;
import org.dlms.services.ScoreResponse;
import org.dlms.services.UserRequest;
import org.dlms.services.UserResponse;
import org.dlms.services.UserServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {
    private static final int PORT = 50052;

    // BIDI-STREAMING
    private EventTrackingServiceGrpc.EventTrackingServiceStub eventTrackingServiceStub;
    // CLIENT-STREAMING
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;
    // SERVER-STREAMING
    private LiveScoreServiceGrpc.LiveScoreServiceStub liveScoreServiceStub;
    // UNIDIRECTIONAL
    private UserServiceGrpc.UserServiceStub userServiceStub;

    private StreamObserver<ActionEvent> actionEventStream;
    private StreamObserver<ImpressionEvent> impressionEventStream;
    private StreamObserver<ChatMessage> chatStream;

    private static GrpcClient instance;
    public static GrpcClient init() {
        if (instance == null) {
            instance = new GrpcClient();
            instance.initialize();
        }
        return instance;
    }

    public static GrpcClient getInstance() {
        return init();
    }

    private void initialize() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("192.168.1.10", PORT)
                .usePlaintext()
                .enableRetry()
                .disableServiceConfigLookUp()
                .defaultServiceConfig(ServiceConfig.createServiceConfig())
                .enableRetry()
                .intercept(new LoggingClientInterceptor())
                .build();
        //GrpcChannelMonitor.monitorChannel(channel);
        eventTrackingServiceStub = EventTrackingServiceGrpc.newStub(channel);
        chatServiceStub = ChatServiceGrpc.newStub(channel);
        liveScoreServiceStub = LiveScoreServiceGrpc.newStub(channel);
        userServiceStub = UserServiceGrpc.newStub(channel);
    }

    public void requestLiveScores(ScoreRequest request, StreamObserver<ScoreResponse> responseObserver) {
        liveScoreServiceStub.getLiveScores(request, responseObserver);
    }

    public void fetchUserDetails(UserRequest userRequest, StreamObserver<UserResponse> responseObserver) {
        userServiceStub.getUser(userRequest, responseObserver);
    }

    void createChatStream() {
        chatStream = chatServiceStub.sendChat(new ChatStreamObserver());
    }

    void createEventStreams() {
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
                createEventStreams();  // Re-establish the stream
                return;  // Exit after successful reconnection
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attempt++;
        }
        System.err.println("Failed to reconnect after " + maxAttempts + " attempts.");
    }

    private StreamObserver<ActionEvent> createImpressionEventStream() {
        return eventTrackingServiceStub.streamAction(new EventResponseObserver("ACTION", this::reconnectStreamWithBackoff));
    }

    private StreamObserver<ImpressionEvent> createActionEventStream() {
        return eventTrackingServiceStub.streamImpression(new EventResponseObserver("IMPRESSION", this::reconnectStreamWithBackoff));
    }

    public StreamObserver<ActionEvent> getActionEventStream() {
        return actionEventStream;
    }

    public StreamObserver<ImpressionEvent> getImpressionEventStream() {
        return impressionEventStream;
    }

    public StreamObserver<ChatMessage> getChatStream() {
        return chatStream;
    }
}
