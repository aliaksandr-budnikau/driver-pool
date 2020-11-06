package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import org.sda.driverpool.entity.RTreeIndex;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@AllArgsConstructor
class RTreeProviderImpl implements RTreeProvider {
    private final BlockingRTreeHolder blockingRTreeHolder;

    @Override
    public RTreeIndex get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        return blockingRTreeHolder.consume(timeout, unit);
    }
}