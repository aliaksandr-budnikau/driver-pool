package org.sda.driverpool.component;

import org.junit.Before;
import org.junit.Test;
import org.sda.driverpool.component.BlockingRTreeHolder;
import org.sda.driverpool.entity.RTreeIndexImpl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.davidmoten.rtree.RTree.create;
import static org.junit.Assert.assertNotNull;

public class BlockingRTreeIndexHolderTest {
    private BlockingRTreeHolder holder;

    @Before
    public void setUp() {
        holder = new BlockingRTreeHolder();
    }

    @Test(expected = TimeoutException.class)
    public void get_whenEmpty() throws InterruptedException, TimeoutException {
        holder.consume(1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void get_whenNotEmpty() throws InterruptedException, TimeoutException {
        holder.produce(new RTreeIndexImpl(create()));
        assertNotNull(holder.consume(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void get_whenEmptyButThenAdded() throws InterruptedException, TimeoutException {
        new Thread(() -> {
            sleep(100);
            holder.produce(new RTreeIndexImpl(create()));
        }).start();
        assertNotNull(holder.consume(1, TimeUnit.SECONDS));
    }

    @Test(expected = TimeoutException.class)
    public void get_whenEmptyButThenAddedButTooLate() throws InterruptedException, TimeoutException {
        new Thread(() -> {
            sleep(10);
            holder.produce(new RTreeIndexImpl(create()));
        }).start();
        holder.consume(1, TimeUnit.MILLISECONDS);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}