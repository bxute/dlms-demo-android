package com.example.nativelib.streamobservers;

import org.dlms.services.ScoreResponse;

import io.grpc.stub.StreamObserver;

public class LiveScoreObserver implements StreamObserver<ScoreResponse> {
    private final ScoreCallback scoreCallback;

    @Override
    public void onNext(ScoreResponse value) {
        this.scoreCallback.onScoreUpdate(value);
    }

    @Override
    public void onError(Throwable t) {
        this.scoreCallback.onError(t);
    }

    @Override
    public void onCompleted() {
        this.scoreCallback.onCompleted();
    }

    public LiveScoreObserver(ScoreCallback scoreCallback) {
        this.scoreCallback = scoreCallback;
    }

    public interface ScoreCallback {
        void onScoreUpdate(ScoreResponse score);
        void onError(Throwable t);
        void onCompleted();
    }
}
