package org.sda.driverpool.component;

import lombok.extern.log4j.Log4j2;
import org.sda.driverpool.entity.RTreeIndex;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
@Component
class BlockingRTreeHolder extends BlockingItemHolder<RTreeIndex> {
    @Override
    public synchronized void produce(RTreeIndex index) {
        log.info("Producing new RTreeIndex with size = {}", index.getSize());
        super.produce(index);
        log.info("new RTreeIndex was set");
    }

    @Override
    public synchronized RTreeIndex consume(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        log.info("Consuming new RTreeIndex");
        RTreeIndex index = super.consume(timeout, unit);
        log.info("new RTreeIndex consumed with size = {}", index.getSize());
        return index;
    }
}