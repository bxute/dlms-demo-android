package com.example.nativelib.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

public class GrpcChannelMonitor {
    public static void monitorChannel(ManagedChannel channel) {
        Executors.newScheduledThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                // Create a ChannelStateChecker
                ChannelStateChecker checker = new ChannelStateChecker(channel);
                // Schedule the state check to run every 2 seconds
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleWithFixedDelay(checker::checkState, 0, 2, TimeUnit.SECONDS);
                // Optionally, keep the main thread alive to continue monitoring
                try {
                    Thread.sleep(600000); // Monitor for 30 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdown();
                    channel.shutdown();
                }
            }
        });
    }
}
