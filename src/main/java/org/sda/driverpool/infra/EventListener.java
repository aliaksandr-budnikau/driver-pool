package org.sda.driverpool.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.sda.driverpool.component.DriverPoolFacade;
import org.sda.driverpool.event.AbstractEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
class EventListener {
    private final DriverPoolFacade facade;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "driver-pool-event-topic")
    public void listen(String message) {
        try {
            Object event = mapper
                    .readerFor(AbstractEvent.class)
                    .readValue(message);
            if (event instanceof OrderPendingDriverEvent) {
                facade.handle((OrderPendingDriverEvent) event);
            } else if (event instanceof RecentDriverStatusUpdateEvent) {
                facade.handle((RecentDriverStatusUpdateEvent) event);
            } else {
                throw new RuntimeException("Can't find handler for " + message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}