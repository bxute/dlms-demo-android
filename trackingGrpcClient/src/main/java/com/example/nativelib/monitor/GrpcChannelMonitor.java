package com.example.nativelib.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

public class GrpcChannelMonitor {
    public static void monitorChannel(ManagedChannel channel) {
        // Create a ChannelStateChecker
        System.out.println("[ " + Thread.currentThread().getName() +" ] Starting channel monitor");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        ChannelStateChecker checker = new ChannelStateChecker(channel);
        executor.execute(() -> {
            System.out.println("[ " + Thread.currentThread().getName() +" ] calling scheduleWithFixedDelay");
            // Schedule the state check to run every 2 seconds
            executor.scheduleWithFixedDelay(checker::checkState, 0, 2, TimeUnit.SECONDS);
            try {
                Thread.sleep(60000); // Monitor for 60 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
                channel.shutdown();
            }
        });
    }
}
