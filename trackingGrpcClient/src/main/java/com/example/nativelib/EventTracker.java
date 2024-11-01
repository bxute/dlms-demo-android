package com.example.nativelib;

import org.dlms.services.ActionEvent;
import org.dlms.services.ImpressionEvent;
import org.jetbrains.annotations.Nullable;

public class EventTracker {
    private final TrackingEventClient client;
    private static EventTracker tracker;

    private EventTracker(TrackingEventClient client) {
        this.client = client;
    }

    public static EventTracker getInstance() {
        if (tracker == null) {
            tracker = new EventTracker(TrackingEventClient.getInstance());
        }
        return tracker;
    }

    public void trackImpressionEvent(@Nullable ImpressionEvent event) {
        client.getImpressionEventStream().onNext(event);
    }

    public void trackActionEvent(@Nullable ActionEvent event) {
        client.getActionEventStream().onNext(event);
    }
}
