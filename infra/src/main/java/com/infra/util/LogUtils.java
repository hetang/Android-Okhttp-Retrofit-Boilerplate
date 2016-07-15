package com.infra.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.annimon.stream.Stream;
import com.infra.logging.LoggingInterface;
import com.infra.logging.factories.LoggingFactory;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class LogUtils {
    private static final String TAG = LogUtils.class.getName();
    protected static List<LoggingInterface> loggingInterfaces = new ArrayList<LoggingInterface>();

    static {
        try{
            loggingInterfaces.add(LoggingFactory.create(LoggingFactory.LoggingTypes.ANDROID_LOG));
            loggingInterfaces.add(LoggingFactory.create(LoggingFactory.LoggingTypes.CRASHLYTICS));
            loggingInterfaces.add(LoggingFactory.create(LoggingFactory.LoggingTypes.HOCKEYAPP));

        }catch (Exception e) {
            Log.e(TAG, "Error while getting factory methods", e);
        }
    }

    /**
     * Use this method to override the default logging interfaces. This should not be used
     * anywhere else but in unit and UI tests.
     *
     * @param newInterfaces the replacement logging interfaces
     */
    public static void overrideLoggingInterfaces(List<LoggingInterface> newInterfaces) {
        loggingInterfaces.clear();
        loggingInterfaces.addAll(newInterfaces);
    }

    public static void initializeLogging(Application application) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.initializeLogging(application);});
    }

    public static void registerForCrash(Context context, int versionCode) {
        if(versionCode != 9999) {
            Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {
                l.registerForCrash(context);
            });
        }
    }

    public static void checkAppUpdate(Activity activity) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.checkAppUpdate(activity);});
    }

    public static void logUserId(String userId) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logUserId(userId);});
    }

    public static void e(String tag, String msg) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logError(tag, msg);});
    }

    public static void e(String tag, String msg, Throwable t) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logError(tag, msg, t);});
    }

    public static void w(String tag, String msg) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logWarn(tag, msg);});
    }

    public static void d(String tag, String msg) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logDebug(tag, msg);});
    }

    public static void i(String tag, String msg) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logInfo(tag, msg);});
    }

    @SuppressWarnings("deprecation")
    // ObjectUtils.toString()'s replacement is java.util.Objects.toString(), but only in API 19+. We're on min API 16.
    public static void recordValue(String key, Object value) {
        Stream.of(loggingInterfaces).forEach((LoggingInterface l) -> {l.logValue(key, ObjectUtils.toString(value));});
    }
}
