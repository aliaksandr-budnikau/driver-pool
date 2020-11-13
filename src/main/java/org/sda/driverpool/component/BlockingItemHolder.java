package org.sda.driverpool.component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
class BlockingItemHolder<T> {
    private volatile T item;

    public void produce(T item) {
        this.item = item;
        notify();
        log.debug("notified");
    }

    public T consume(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        long timeoutMillis = unit.toMillis(timeout);
        if (item == null) {
            log.debug("Waiting timeoutMillis={}", timeoutMillis);
            wait(timeoutMillis);
            log.debug("Stopped waiting");
            if (item == null) {
                log.debug("Still null");
                throw new TimeoutException("timeoutMillis=" + timeoutMillis + " elapsed");
            }
        }
        return item;
    }
}