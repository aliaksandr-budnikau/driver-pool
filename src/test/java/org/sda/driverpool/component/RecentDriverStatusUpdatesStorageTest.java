package org.sda.driverpool.component;

import org.junit.Before;
import org.junit.Test;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.LinkedList;

import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RecentDriverStatusUpdatesStorageTest {

    private RecentDriverStatusUpdatesStorage storage;
    private LinkedList<RecentDriverStatusUpdate> recentDriverStatusUpdatesList;

    @Before
    public void setUp() {
        recentDriverStatusUpdatesList = new LinkedList<>();
        KafkaTemplate<String, RecentDriverStatusUpdate> template = mock(KafkaTemplate.class);
        storage = new RecentDriverStatusUpdatesStorageImpl(recentDriverStatusUpdatesList, template);
    }

    @Test
    public void addRecentDriverStatusUpdate() {
        RecentDriverStatusUpdate update = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER, "qweqwe");
        storage.add(update);
        assertEquals(recentDriverStatusUpdatesList.getFirst(), update);
        assertEquals(recentDriverStatusUpdatesList.getLast(), update);
        assertEquals(recentDriverStatusUpdatesList.size(), 1);
    }

    @Test
    public void getRecentDriverStatusUpdates() {
        range(0, 10).forEach(i -> recentDriverStatusUpdatesList.addLast(new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER, "qweqwe")));
        range(0, 10).forEach(i -> recentDriverStatusUpdatesList.addLast(new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, DriverStatus.ON_RIDE, "qweqwe")));
        assertEquals(storage.getAll().size(), 2);
    }
}
