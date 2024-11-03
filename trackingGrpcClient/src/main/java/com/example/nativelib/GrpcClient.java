package com.example.nativelib;

import android.content.Context;

import com.example.nativelib.interceptors.LoggingClientInterceptor;
import com.example.nativelib.monitor.GrpcChannelMonitor;
import com.example.nativelib.serviceconfig.ServiceConfig;
import com.example.nativelib.streamobservers.ChatStreamObserver;
import com.example.nativelib.streamobservers.EventResponseObserver;
import com.example.trackingGrpcClient.R;

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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {
    private static final String DOMAIN = "192.168.1.10";
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

    public static void init(Context appContext) {
        if (instance == null) {
            instance = new GrpcClient();
            instance.initialize(appContext);
        }
    }

    public static GrpcClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GrpcClient not initialized, call init() in Application class.");
        }
        return instance;
    }

    private void initialize(Context appContext) {
        ManagedChannel channel = createChannel(appContext, true);
        GrpcChannelMonitor.monitorChannel(channel);
        eventTrackingServiceStub = EventTrackingServiceGrpc.newStub(channel);
        chatServiceStub = ChatServiceGrpc.newStub(channel);
        liveScoreServiceStub = LiveScoreServiceGrpc.newStub(channel);
        userServiceStub = UserServiceGrpc.newStub(channel);
    }

    private ManagedChannel createChannel(Context appContext, boolean sslEnabled) {
        if (sslEnabled) {
            try {
                CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
                InputStream certsInputStream = appContext.getResources().openRawResource(R.raw.ca);
                Certificate certificate = certificateFactory.generateCertificate(certsInputStream);
                certsInputStream.close();

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", certificate);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);

                return OkHttpChannelBuilder.forAddress(DOMAIN, PORT)
                        .sslSocketFactory(sslContext.getSocketFactory())
                        .useTransportSecurity()
                        .hostnameVerifier((s, sslSession) -> true)
                        .disableServiceConfigLookUp()
                        .defaultServiceConfig(ServiceConfig.createServiceConfig())
                        .enableRetry()
                        .intercept(new LoggingClientInterceptor())
                        .build();

            } catch (IOException | KeyStoreException | CertificateException |
                     NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }

        // build unsecured channel
        return ManagedChannelBuilder
                .forAddress(DOMAIN, PORT)
                .usePlaintext()
                .disableServiceConfigLookUp()
                .defaultServiceConfig(ServiceConfig.createServiceConfig())
                .enableRetry()
                .intercept(new LoggingClientInterceptor())
                .build();
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
