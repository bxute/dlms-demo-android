package com.example.nativelib;

import android.util.Log;

import org.dlms.services.TrackEventResponse;

import io.grpc.stub.StreamObserver;

public class EventResponseObserver implements StreamObserver<TrackEventResponse> {
    @Override
    public void onNext(TrackEventResponse value) {
        Log.d("EventResponseObserver", "Response : " + value.getMessage());
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onCompleted() {
        Log.d("EventResponseObserver", "onCompleted: ");
    }
}
