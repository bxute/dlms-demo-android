package com.example.nativelib.monitor;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;

public class ChannelStateChecker {
    private final ManagedChannel channel;

    public ChannelStateChecker(ManagedChannel channel) {
        this.channel = channel;
    }

    public void checkState() {
        ConnectivityState state = channel.getState(true); // true to request connection if IDLE
        System.out.println("[ " + Thread.currentThread().getName() +" ] Channel state: " + state);
    }
}
