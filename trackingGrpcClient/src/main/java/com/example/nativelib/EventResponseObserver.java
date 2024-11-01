package com.example.nativelib;

import android.util.Log;

import org.dlms.services.TrackEventResponse;

import io.grpc.stub.StreamObserver;

public class EventResponseObserver implements StreamObserver<TrackEventResponse> {
    private final OnErrorCallback error;
    private String tag;
    @Override
    public void onNext(TrackEventResponse value) {
        Log.d("EventResponseObserver", "["+tag+"] Response : " + value.getMessage());
    }

    @Override
    public void onError(Throwable t) {
        Log.d("EventResponseObserver", "onError: " + tag);
        if (error != null) {
            error.onError();
        }
    }

    @Override
    public void onCompleted() {
        Log.d("EventResponseObserver", "onCompleted: ");
    }

    public EventResponseObserver(String tag, OnErrorCallback error) {
        this.tag = tag;
        this.error = error;
    }

    interface OnErrorCallback {
        void onError();
    }
}
