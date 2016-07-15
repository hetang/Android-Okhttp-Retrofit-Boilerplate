package com.infra.logging;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by hetashah on 3/23/16.
 */
public interface LoggingInterface {
    boolean isEnabled();
    void initializeLogging(Application application);
    void registerForCrash(Context context);
    void checkAppUpdate(Activity activity);
    void logUserId(String userId);
    void logError(String tag, String msg, Throwable e);
    void logError(String tag, String msg);
    void logWarn(String tag, String msg);
    void logDebug(String tag, String msg);
    void logInfo(String tag, String msg);
    void logValue(String key, String value);
    void logValue(String key, boolean value);
    void logValue(String key, double value);
    void logValue(String key, float value);
    void logValue(String key, int value);
}
