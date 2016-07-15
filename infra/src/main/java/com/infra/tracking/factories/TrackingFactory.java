package com.infra.tracking.factories;

import com.infra.tracking.TrackingInterface;
import com.infra.tracking.impl.SampleImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetashah on 3/21/16.
 */
public class TrackingFactory {
    private final static Map<TrackingTypes, Class<? extends TrackingInterface>> trackingMap =
            new HashMap<TrackingTypes, Class<? extends TrackingInterface>>(TrackingTypes.values().length, 1.0f);
    static {
        trackingMap.put(TrackingTypes.SAMPLE, SampleImpl.class); //TODO: Add your analytics implementation
    }

    public enum TrackingTypes {
        SAMPLE,
        MOCK;
    }

    public static TrackingInterface create(TrackingTypes trackingTypes) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = trackingMap.get(trackingTypes);
        if (Arrays.asList(clazz.getInterfaces()).contains(TrackingInterface.class)){
            return (TrackingInterface) clazz.newInstance();
        }
        throw new IllegalArgumentException("Provided class doesn't implement TrackingInterface interface");
    }

    private TrackingFactory() {
        // avoid instantiation
    }

    static Map<TrackingTypes, Class<? extends TrackingInterface>> getTrackingMap() {
        return trackingMap;
    }
}
