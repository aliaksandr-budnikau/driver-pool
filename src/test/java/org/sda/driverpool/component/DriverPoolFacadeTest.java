package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        RecentDriverStatusUpdateEvent event = new RecentDriverStatusUpdateEvent(driverId, 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER);
        RecentDriverStatusUpdate update = new RecentDriverStatusUpdate(driverId, 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER, "qweqwe");
        when(recentDriverStatusUpdateFactory.get(driverId, 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER))
                .thenReturn(update);
        facade.handle(event);
        verify(storage).add(update);
    }

    @Test
    public void handleOrderPendingDriverEvent_thereIsOneDriver() throws InterruptedException, TimeoutException {
        RecentDriverStatusUpdate statusUpdate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, DriverStatus.PENDING_ORDER, "qweqwe");
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
}