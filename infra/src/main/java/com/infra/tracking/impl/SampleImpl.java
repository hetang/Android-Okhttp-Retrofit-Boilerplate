package com.infra.tracking.impl;

import android.app.Application;

import com.infra.tracking.TrackingInterface;

import java.util.Map;

/**
 * Created by hetashah on 7/15/16.
 */
public class SampleImpl implements TrackingInterface {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void initializeTracking(Application application, int versionCode) {

    }

    @Override
    public void trackEvents(String tag, Map<String, Object> events) {

    }

    @Override
    public void trackAttributes(String tag, String event) {

    }

    @Override
    public void trackPageImpression(String name) {

    }

    @Override
    public void trackUserId(String userId) {

    }

    @Override
    public void logoutUser() {

    }

    @Override
    public void sendDataNow() {

    }
}
