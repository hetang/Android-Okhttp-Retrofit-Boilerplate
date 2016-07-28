package com.infra.managers.requests;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by hetashah on 7/17/16.
 */

public class RetryWithDelay implements
        Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts
                .flatMap((Throwable throwable) -> {
                    if (++retryCount < maxRetries) {
                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed).
                        return Observable.timer((retryDelayMillis * retryCount),
                                TimeUnit.MILLISECONDS);
                    }

                    // Max retries hit. Just pass the error along.
                    return Observable.error(throwable);
                });
    }
}
