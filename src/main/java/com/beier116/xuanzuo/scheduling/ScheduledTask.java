package com.beier116.xuanzuo.scheduling;

import java.util.concurrent.ScheduledFuture;

public final class ScheduledTask {

    volatile ScheduledFuture<?> future;

    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }
}
