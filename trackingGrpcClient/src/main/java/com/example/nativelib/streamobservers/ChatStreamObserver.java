package com.example.nativelib.streamobservers;

import org.dlms.services.ChatResponse;

import io.grpc.stub.StreamObserver;

public class ChatStreamObserver implements StreamObserver<ChatResponse> {
    @Override
    public void onNext(ChatResponse value) {
        System.out.println("ChatResponse Received response: " + value.getConfirmation());
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("ChatResponse Error occurred: " + t.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("ChatResponse Stream completed.");
    }
}
