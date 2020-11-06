package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sda.driverpool.entity.RTreeIndexImpl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RTreeIndexProviderTest {
    @Mock
    private BlockingRTreeHolder holder;
    @InjectMocks
    private RTreeProviderImpl provider;

    @Test(expected = TimeoutException.class)
    public void get_TimeoutException() throws InterruptedException, TimeoutException {
        when(holder.consume(anyLong(), any(TimeUnit.class))).thenThrow(TimeoutException.class);
        provider.get(1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void get_whenNotEmpty() throws InterruptedException, TimeoutException {
        when(holder.consume(anyLong(), any(TimeUnit.class))).thenReturn(new RTreeIndexImpl(null));
        assertNotNull(provider.get(1, TimeUnit.MILLISECONDS));
    }
}