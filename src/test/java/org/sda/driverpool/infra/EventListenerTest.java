package org.sda.driverpool.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.sda.driverpool.component.DriverPoolFacade;
import org.sda.driverpool.entity.DriverStatus;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventListenerTest {

    private EventListener listener;
    private DriverPoolFacade facade;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        facade = mock(DriverPoolFacade.class);
        mapper = new SerializationConfig().objectMapper();
        listener = new EventListener(facade, mapper);
    }

    @Test
    public void listen_OrderPendingDriverEvent() throws JsonProcessingException, InterruptedException {
        OrderPendingDriverEvent event = new OrderPendingDriverEvent(10f, 20f, 40d);
        String message = mapper.writeValueAsString(event);
        listener.listen(message);

        verify(facade).handle(any(event.getClass()));
    }

    @Test
    public void listen_RecentDriverStatusUpdateEvent() throws JsonProcessingException, InterruptedException {
        RecentDriverStatusUpdateEvent event = new RecentDriverStatusUpdateEvent("driver_id", 10f, 20f, DriverStatus.ON_RIDE);
        String message = mapper.writeValueAsString(event);
        listener.listen(message);

        verify(facade).handle(any(event.getClass()));
    }

    @Test(expected = RuntimeException.class)
    public void listen_unknownEvent() throws JsonProcessingException {
        OrderGotDriverEvent event = new OrderGotDriverEvent("driver_id");
        String message = mapper.writeValueAsString(event);
        listener.listen(message);
    }

    @Test(expected = RuntimeException.class)
    public void listen_someString() throws JsonProcessingException {
        String message = mapper.writeValueAsString("");
        listener.listen(message);
    }
}