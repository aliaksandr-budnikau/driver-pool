package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RTreeIndexImpl;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RTreeIndexRefresherTest {
    @Mock
    private RTreeFactory rTreeFactory;
    @Mock
    private BlockingRTreeHolder holder;
    @Mock
    private RecentDriverStatusUpdatesStorage storage;
    @InjectMocks
    private RTreeRefresher refresher;

    @Test
    public void refresh() {
        HashSet<RecentDriverStatusUpdate> set = new HashSet<>();
        when(storage.getAll(true)).thenReturn(set);
        when(rTreeFactory.create(set)).thenReturn(new RTreeIndexImpl(null));
        refresher.refresh();
        verify(holder).produce(any(RTreeIndex.class));
    }
}
