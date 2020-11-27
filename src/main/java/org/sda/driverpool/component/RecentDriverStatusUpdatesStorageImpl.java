package org.sda.driverpool.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Component
@RequiredArgsConstructor
class RecentDriverStatusUpdatesStorageImpl implements RecentDriverStatusUpdatesStorage {
    private final KafkaConsumer<String, RecentDriverStatusUpdate> consumer;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private volatile CachedGetAllResult cachedGetAllResult;

    @Value("${kafka.recentDriverStatusUpdateTopic}")
    private String recentDriverStatusUpdateTopic;
    @Value("${kafka.recentDriverStatusUpdateTopicRelevantLengthPerPartition}")
    private int recentDriverStatusUpdateTopicRelevantLengthPerPartition;
    @Value("${kafka.allRecentDriverStatusUpdateCacheDurabilityTimeInSeconds:10}")
    private int allRecentDriverStatusUpdateCacheDurabilityTimeInSeconds;

    @Override
    public Set<RecentDriverStatusUpdate> getAll(boolean fromCache) {
        return new HashSet<>(getRecentDriverStatusUpdateStream(fromCache));
    }

    @Override
    public void add(RecentDriverStatusUpdate update) {
        kafkaTemplate.send(recentDriverStatusUpdateTopic, update.getDriverId(), update);
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
        return getRecentDriverStatusUpdateStream(false)
                .stream()
                .filter(driver -> driverId.equals(driver.getDriverId()))
                .limit(count)
                .collect(toList());
    }

    private List<RecentDriverStatusUpdate> getRecentDriverStatusUpdateStream(boolean fromCache) {
        if (shouldUseCache(fromCache)) {
            return cachedGetAllResult.getResult();
        }

        ConsumerRecords<String, RecentDriverStatusUpdate> poll;
        synchronized (this) {
            if (shouldUseCache(fromCache)) {
                return cachedGetAllResult.getResult();
            }
            Set<TopicPartition> partitions = consumer.assignment();
            consumer.seekToEnd(partitions);
            partitions.forEach(key -> {
                long offset = consumer.position(key) - recentDriverStatusUpdateTopicRelevantLengthPerPartition;
                consumer.seek(key, offset < 0 ? 0 : offset);
            });
            poll = consumer.poll(of(100, MILLIS));
        }

        List<RecentDriverStatusUpdate> result = stream(poll.spliterator(), false)
                .map(ConsumerRecord::value)
                .collect(reverse())
                .collect(toList());

        if (result.isEmpty()) {
            return result;
        }
        cachedGetAllResult = new CachedGetAllResult(currentTimeMillis(), result);
        return result;
    }

    private boolean shouldUseCache(boolean fromCache) {
        return fromCache && cachedGetAllResult != null &&
                currentTimeMillis() - cachedGetAllResult.getTimestamp() <= allRecentDriverStatusUpdateCacheDurabilityTimeInSeconds * 1000;
    }

    private <T> Collector<T, ?, Stream<T>> reverse() {
        return Collectors.collectingAndThen(toList(), list -> {
            Collections.reverse(list);
            return list.stream();
        });
    }

    @Getter
    @AllArgsConstructor
    private static class CachedGetAllResult {
        private final long timestamp;
        private final List<RecentDriverStatusUpdate> result;
    }
}
