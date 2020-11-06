package org.sda.driverpool.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sda.driverpool.entity.DriverStatus.ON_RIDE;
import static org.sda.driverpool.entity.DriverStatus.PENDING_ORDER;

@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest {
    @Mock
    private RecentDriverStatusUpdatesStorage storage;
    @Mock
    private RecentDriverStatusUpdateFactory recentDriverStatusUpdateFactory;
    @Mock
    private CurrentNodeMetaDataProvider currentNodeMetaDataProvider;
    @InjectMocks
    private BookingServiceImpl service;

    @Test
    public void tryBooking_actualStatusWasntFound() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenThrow(NothingFoundException.class);

        assertFalse(service.tryBooking(candidate));
        verify(storage, times(0)).add(any(RecentDriverStatusUpdate.class));
    }

    @Test
    public void tryBooking_candidateIsAlreadyBusy() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));

        assertFalse(service.tryBooking(candidate));
        verify(storage, times(0)).add(any(RecentDriverStatusUpdate.class));
    }

    @Test
    public void tryBooking_insufficientAmountOfDataForStrictlyBooking() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), candidate.getStatus(), "qweqwe"));
        when(storage.getById(candidate.getDriverId(), 2)).thenReturn(Collections.emptyList());
        when(recentDriverStatusUpdateFactory.get(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE))
                .thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));

        assertFalse(service.tryBooking(candidate));
        verify(storage).add(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
    }

    @Test
    public void tryBooking_candidateHasWrongActualStatus() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), null, "qweqwe"));

        assertFalse(service.tryBooking(candidate));
        verify(storage, times(0)).add(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
    }

    @Test
    public void tryBooking_candidateIsProbablyBusy() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), candidate.getStatus(), "qweqwe"));
        when(storage.getById(candidate.getDriverId(), 2)).thenReturn(asList(candidate, candidate));
        when(recentDriverStatusUpdateFactory.get(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE))
                .thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
        when(currentNodeMetaDataProvider.getCurrentNodeId()).thenReturn("qweqwe");
        assertFalse(service.tryBooking(candidate));
        verify(storage).add(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
    }

    @Test
    public void tryBooking_candidateIsProbablyBusy_wrongNodeIds() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        RecentDriverStatusUpdate candidateWasTakeOnAnotherNode = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, ON_RIDE, "asdasd");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), candidate.getStatus(), "qweqwe"));
        when(storage.getById(candidate.getDriverId(), 2)).thenReturn(asList(candidateWasTakeOnAnotherNode, candidate));
        when(recentDriverStatusUpdateFactory.get(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE))
                .thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
        when(currentNodeMetaDataProvider.getCurrentNodeId()).thenReturn("qweqwe");

        assertFalse(service.tryBooking(candidate));
        verify(storage).add(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
    }

    @Test
    public void tryBooking_allIsOkBookedStrictly() throws NothingFoundException {
        RecentDriverStatusUpdate candidate = new RecentDriverStatusUpdate("driver_id", 53.897920f, 27.562034f, PENDING_ORDER, "qweqwe");
        when(storage.getById(candidate.getDriverId())).thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), candidate.getStatus(), "qweqwe"));
        when(storage.getById(candidate.getDriverId(), 2)).thenReturn(
                asList(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"), candidate)
        );
        when(recentDriverStatusUpdateFactory.get(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE))
                .thenReturn(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
        when(currentNodeMetaDataProvider.getCurrentNodeId()).thenReturn("qweqwe");

        assertTrue(service.tryBooking(candidate));
        verify(storage).add(new RecentDriverStatusUpdate(candidate.getDriverId(), candidate.getLatitude(), candidate.getLongitude(), ON_RIDE, "qweqwe"));
    }
}