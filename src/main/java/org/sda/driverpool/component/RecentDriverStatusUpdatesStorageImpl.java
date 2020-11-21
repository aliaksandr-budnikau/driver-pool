package org.sda.driverpool.component;

import lombok.RequiredArgsConstructor;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
class RecentDriverStatusUpdatesStorageImpl implements RecentDriverStatusUpdatesStorage {
    private final LinkedList<RecentDriverStatusUpdate> recentDriverStatusUpdatesList;
    private final KafkaTemplate<String, RecentDriverStatusUpdate> kafkaTemplate;

    @Value(value = "${kafka.recentDriverStatusUpdateTopic}")
    private String recentDriverStatusUpdateTopic;

    @Override
    public Set<RecentDriverStatusUpdate> getAll() {
        return new HashSet<>(recentDriverStatusUpdatesList);
    }

    @Override
    public void add(RecentDriverStatusUpdate update) {
        kafkaTemplate.send(recentDriverStatusUpdateTopic, update.getDriverId(), update);
        recentDriverStatusUpdatesList.addFirst(update);
    }

    @Override
    public RecentDriverStatusUpdate getById(String driverId) throws NothingFoundException {
        return getById(driverId, 1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NothingFoundException("Status for driver " + driverId + " wasn't found"));
    }

    @Override
    public List<RecentDriverStatusUpdate> getById(String driverId, int count) {
        return recentDriverStatusUpdatesList.stream()
                .filter(driver -> driverId.equals(driver.getDriverId()))
                .limit(count)
                .collect(toList());
    }
}
