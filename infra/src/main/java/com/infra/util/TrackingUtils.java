package com.infra.util;

import android.app.Application;

import com.annimon.stream.Stream;
import com.infra.tracking.TrackingInterface;
import com.infra.tracking.factories.TrackingFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingUtils {
    private static final String TAG = TrackingUtils.class.getName();
    protected List<TrackingInterface> trackingInterfaces = new ArrayList<TrackingInterface>();

    private static class SingletonHolder {
        static TrackingUtils INSTANCE;
    }

    protected TrackingUtils() {
        this.addTrackingImpl();
    }

    public static TrackingUtils getInstance() {
        if(SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new TrackingUtils();
        }

        return SingletonHolder.INSTANCE;
    }

    public static TrackingUtils getInstance(TrackingUtils override) {
        if(override != null) {
            SingletonHolder.INSTANCE = override;
        } else if(SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new TrackingUtils();
        }

        return SingletonHolder.INSTANCE;
    }

    protected void addTrackingImpl(){
        try{
            trackingInterfaces.add(TrackingFactory.create(TrackingFactory.TrackingTypes.SAMPLE));

        }catch (Exception e) {
            LogUtils.e(TAG, "Error while getting factory methods", e);
        }
    }

    public void initializeTracking(Application application, final int versionCode) {
        try {
            Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {
                i.initializeTracking(application, versionCode);
            });

            track("Android N1 App Launched", new HashMap<>());
        } catch(VirtualMachineError error) {
            throw error;
        } catch(Throwable t) {
            LogUtils.e(TAG, "", t);
        }
    }

    public void trackUserId(String userId) {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {i.trackUserId(userId);});
    }

    public void trackLogout() {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {i.logoutUser();});
    }

    public void track(String key, String value) {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {i.trackAttributes(key, value);});
    }

    public void track(String key, String... values) {
        if(values != null && values.length > 0) {
            for(String value : values) {
                track(key, value);
            }
        }
    }

    public void track(String key, Map<String,Object> value) {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {
            try {
                i.trackEvents(key, value);
            } catch(Exception ex) {
              LogUtils.e(TAG, "Error while tracking with " + i.getClass(), ex);
            }
        });
    }

    public void trackViewImpression(String name) {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {
            try {
                i.trackPageImpression(name);
            } catch(Exception ex) {
                LogUtils.e(TAG, "Error while tracking with " + i.getClass(), ex);
            }
        });
    }

    public void publishAllData() {
        Stream.of(trackingInterfaces).forEach((TrackingInterface i) -> {
            try {
                i.sendDataNow();
            } catch(Exception ex) {
            LogUtils.e(TAG, "Error while tracking with " + i.getClass(), ex);
        }
        });
    }
}
