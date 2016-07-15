package com.infra.logging.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.infra.logging.LoggingInterface;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by hetashah on 3/30/16.
 */
public class AndroidLogImpl implements LoggingInterface {
    private final String TAG = AndroidLogImpl.class.getName();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void initializeLogging(Application application) {
        //Nothing to do here
    }

    @Override
    public void registerForCrash(Context context) {
        //Nothing to do here
    }

    @Override
    public void checkAppUpdate(Activity activity) {
        //Nothing to do here
    }

    @Override
    public void logUserId(String userId) {
        if(isEnabled()) {
            Log.i(TAG, userId);
        }
    }

    @Override
    public void logError(String tag, String msg, Throwable e) {
        if(isEnabled()) {
            Log.e(tag, msg, e);

            // Recursively print the causes, something that Log.e() doesn't do
            if(e.getCause() != null) {
                Log.e(tag, "Caused by: " + StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), SystemUtils.LINE_SEPARATOR));
            }
        }
    }

    @Override
    public void logError(String tag, String msg) {
        if(isEnabled()) {
            Log.e(tag, msg);
        }
    }

    @Override
    public void logWarn(String tag, String msg) {
        if(isEnabled()) {
            Log.w(tag, msg);
        }
    }

    @Override
    public void logDebug(String tag, String msg) {
        if(isEnabled()) {
            Log.d(tag, msg);
        }
    }

    @Override
    public void logInfo(String tag, String msg) {
        if(isEnabled()) {
            Log.i(tag, msg);
        }
    }

    @Override
    public void logValue(String key, String value) {
        if(isEnabled()) {
            Log.i(key, value);
        }
    }

    @Override
    public void logValue(String key, boolean value) {
        if(isEnabled()) {
            Log.i(key, String.valueOf(value));
        }
    }

    @Override
    public void logValue(String key, double value) {
        if(isEnabled()) {
            Log.i(key, String.valueOf(value));
        }
    }

    @Override
    public void logValue(String key, float value) {
        if(isEnabled()) {
            Log.i(key, String.valueOf(value));
        }
    }

    @Override
    public void logValue(String key, int value) {
        if(isEnabled()) {
            Log.i(key, String.valueOf(value));
        }
    }
}
