package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sda.driverpool.dto.RecentDriverStatusUpdateDTO;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sda.driverpool.entity.DriverStatus.ON_RIDE;
import static org.sda.driverpool.entity.DriverStatus.PENDING_ORDER;

@RunWith(MockitoJUnitRunner.class)
public class DriverPoolFacadeTest {
    @Mock
    private RTreeIndex rTree;
    @Mock
    private RTreeProvider rTreeProvider;
    @Mock
    private RecentDriverStatusUpdatesStorage storage;
    @Mock
    private EventSender eventSender;
    @Mock
    private BookingService bookingService;
    @Mock
    private RecentDriverStatusUpdateFactory recentDriverStatusUpdateFactory;
    @InjectMocks
    private DriverPoolFacade facade;

    @Test
    public void handleDriverTickEvent() {
        String driverId = "driver_id";
        RecentDriverStatusUpdateEvent event = new RecentDriverStatusUpdateEvent(driverId, 53.897920f, 27.562034f, PENDING_ORDER);
        RecentDriverStatusUpdate update = new RecentDriverStatusUpdate(driverId, 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(recentDriverStatusUpdateFactory.get(driverId, 53.897920f, 27.562034f, PENDING_ORDER))
                .thenReturn(update);
        facade.handle(event);
        verify(storage).add(update);
    }

    @Test
    public void handleOrderPendingDriverEvent_thereIsOneDriver() throws InterruptedException, TimeoutException {
        RecentDriverStatusUpdate statusUpdate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(rTree.getClosest(anyFloat(), anyFloat(), anyDouble(), anyInt())).thenReturn(asList(statusUpdate));
        when(rTreeProvider.get(anyLong(), any(TimeUnit.class))).thenReturn(rTree);
        when(bookingService.tryBooking(any(RecentDriverStatusUpdate.class))).thenReturn(true);

        OrderPendingDriverEvent event = new OrderPendingDriverEvent(53.897920f, 27.562034f, 0);
        facade.handle(event);

        ArgumentCaptor<OrderGotDriverEvent> captor = ArgumentCaptor.forClass(OrderGotDriverEvent.class);
        verify(eventSender).send(captor.capture());
        OrderGotDriverEvent outEvent = captor.getValue();
        assertEquals("driver_id", outEvent.getDriverId());
    }

    @Test
    public void getAllDrivers() {
        RecentDriverStatusUpdate d1 = new RecentDriverStatusUpdate("d1", 1, 1, PENDING_ORDER, "e1");
        RecentDriverStatusUpdate d2 = new RecentDriverStatusUpdate("d2", 2, 2, ON_RIDE, "e2");

        when(storage.getAll(false)).thenReturn(new HashSet<>(asList(d2, d1)));

        Set<RecentDriverStatusUpdateDTO> drivers = facade.getAllDrivers(false);

        assertNotNull(drivers);


        Iterator<RecentDriverStatusUpdateDTO> iterator = drivers.iterator();
        iterator.next();
        RecentDriverStatusUpdateDTO dto1 = iterator.next();
        assertEquals(d1.getDriverId(), dto1.getDriverId());
        assertEquals(d1.getLatitude(), dto1.getLatitude(), 0);
        assertEquals(d1.getLongitude(), dto1.getLongitude(), 0);
        assertEquals(d1.getEventId(), dto1.getEventId());
        assertEquals(d1.getStatus().name(), dto1.getStatus());

        iterator = drivers.iterator();
        RecentDriverStatusUpdateDTO dto2 = iterator.next();
        assertEquals(d2.getDriverId(), dto2.getDriverId());
        assertEquals(d2.getLatitude(), dto2.getLatitude(), 0);
        assertEquals(d2.getLongitude(), dto2.getLongitude(), 0);
        assertEquals(d2.getEventId(), dto2.getEventId());
        assertEquals(d2.getStatus().name(), dto2.getStatus());
    }

    @Test
    public void getAllDrivers_whenEmpty() {
        Set<RecentDriverStatusUpdateDTO> drivers = facade.getAllDrivers(false);

        assertNotNull(drivers);
        assertTrue(drivers.isEmpty());
    }
}
