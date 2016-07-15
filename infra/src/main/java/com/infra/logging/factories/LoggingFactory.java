package com.infra.logging.factories;

import com.infra.logging.LoggingInterface;
import com.infra.logging.impl.AndroidLogImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hetashah on 3/23/16.
 */
public class LoggingFactory {

    private final static Map<LoggingTypes, Class<? extends LoggingInterface>> loggingMap =
            new HashMap<LoggingTypes, Class<? extends LoggingInterface>>(LoggingTypes.values().length, 1.0f);
    static {
        loggingMap.put(LoggingTypes.ANDROID_LOG, AndroidLogImpl.class);
    }

    public enum LoggingTypes {
        HOCKEYAPP,
        CRASHLYTICS,
        ANDROID_LOG,
        MOCK;
    }

    public static LoggingInterface create(LoggingTypes type) throws IllegalAccessException, InstantiationException {
        Class<?> clazz = loggingMap.get(type);
        if(clazz != null) {
            if (Arrays.asList(clazz.getInterfaces()).contains(LoggingInterface.class)) {
                return (LoggingInterface) clazz.newInstance();
            }
            throw new IllegalArgumentException("Provided class doesn't implement TrackingInterface interface");
        }
        return null;
    }

    private LoggingFactory() {
        // avoid instantiation
    }

    static Map<LoggingTypes, Class<? extends LoggingInterface>> getLoggingMap() {
        return loggingMap;
    }
}
