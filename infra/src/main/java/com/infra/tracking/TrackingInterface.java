package com.infra.tracking;

import android.app.Application;

import java.util.Map;

/**
 * Created by hetashah on 3/21/16.
 */
public interface TrackingInterface {
    public boolean isEnabled();
    public void initializeTracking(Application application, int versionCode);
    public void trackEvents(String tag, Map<String, Object> events);
    public void trackAttributes(String tag, String event);
    public void trackPageImpression(String name);
    public void trackUserId(String userId);
    public void logoutUser();
    public void sendDataNow();
}
