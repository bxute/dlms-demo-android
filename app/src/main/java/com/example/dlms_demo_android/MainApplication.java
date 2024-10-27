package com.example.dlms_demo_android;

import android.app.Application;

import com.example.nativelib.TrackingEventClient;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TrackingEventClient.init();
    }
}
