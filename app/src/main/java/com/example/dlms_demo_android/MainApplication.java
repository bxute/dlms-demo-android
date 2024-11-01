package com.example.dlms_demo_android;

import android.app.Application;

import com.example.nativelib.GrpcClient;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GrpcClient.init();
    }
}
