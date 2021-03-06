package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sda.driverpool.dto.RecentDriverStatusUpdateDTO;
import org.sda.driverpool.entity.RTreeIndex;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.sda.driverpool.event.OrderGotDriverEvent;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@AllArgsConstructor
@Slf4j
public class DriverPoolFacade {
    private final RTreeProvider rTreeProvider;
    private final RecentDriverStatusUpdatesStorage storage;
    private final EventSender eventSender;
    private final BookingService bookingService;
    private final RecentDriverStatusUpdateFactory recentDriverStatusUpdateFactory;
    //@Value("org.sda.driverpool.rtreeGetting.timeoutInMilliseconds")
    private final int timeoutInMilliseconds = 5000;

    @KafkaListener(topics = "${kafka.recentDriverStatusUpdateEventTopic}", containerFactory = "recentDriverStatusUpdateEventTopicListenerContainerFactory")
    public void handle(RecentDriverStatusUpdateEvent event) {
        RecentDriverStatusUpdate update = recentDriverStatusUpdateFactory.get(
                event.getDriverId(),
                event.getLatitude(),
                event.getLongitude(),
                event.getStatus()
        );
        log.debug("Adding status update {}", update);
        storage.add(update);
        log.debug("Added");
    }

    @KafkaListener(topics = "${kafka.orderPendingDriverEventTopic}", containerFactory = "orderPendingDriverEventTopicListenerContainerFactory")
    public void handle(OrderPendingDriverEvent event) throws InterruptedException {
        log.info("Driver finding {}", event);
        RTreeIndex rTree;
        try {
            rTree = rTreeProvider.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        log.info("Traversing candidates for order");
        List<RecentDriverStatusUpdate> closest = rTree.getClosest(event.getLatitude(), event.getLongitude(), event.getMaxDistance(), 10);
        log.info("Found {} for traversing", closest.size());
        for (RecentDriverStatusUpdate candidate : closest) {
            if (!bookingService.tryBooking(candidate)) {
                continue;
            }

            eventSender.send(new OrderGotDriverEvent(candidate.getDriverId()));

            log.info("Traversing finished");
            return;
        }
        log.info("Finished traversing");
    }

    public Set<RecentDriverStatusUpdateDTO> getAllDrivers(boolean fromCache) {
        Set<RecentDriverStatusUpdate> all = storage.getAll(fromCache);
        Set<RecentDriverStatusUpdateDTO> result = new HashSet<>();
        for (RecentDriverStatusUpdate update : all) {
            RecentDriverStatusUpdateDTO dto = new RecentDriverStatusUpdateDTO();
            dto.setDriverId(update.getDriverId());
            dto.setLatitude(update.getLatitude());
            dto.setLongitude(update.getLongitude());
            dto.setEventId(update.getEventId());
            dto.setStatus(update.getStatus().name());
            result.add(dto);
        }
        return result;
    }
}
