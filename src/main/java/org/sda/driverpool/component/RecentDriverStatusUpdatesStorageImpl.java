package org.sda.driverpool.component;

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
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.Duration.of;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@Component
@RequiredArgsConstructor
class RecentDriverStatusUpdatesStorageImpl implements RecentDriverStatusUpdatesStorage {
    private final KafkaConsumer<String, RecentDriverStatusUpdate> consumer;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.recentDriverStatusUpdateTopic}")
    private String recentDriverStatusUpdateTopic;
    @Value("${kafka.recentDriverStatusUpdateTopicRelevantLengthPerPartition}")
    private int recentDriverStatusUpdateTopicRelevantLengthPerPartition;

    @Override
    public Set<RecentDriverStatusUpdate> getAll() {
        return getRecentDriverStatusUpdateStream()
                .collect(Collectors.toSet());
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
        return getRecentDriverStatusUpdateStream()
                .filter(driver -> driverId.equals(driver.getDriverId()))
                .limit(count)
                .collect(toList());
    }

    private synchronized Stream<RecentDriverStatusUpdate> getRecentDriverStatusUpdateStream() {
        Set<TopicPartition> partitions = consumer.assignment();
        consumer.seekToEnd(partitions);
        partitions.forEach(key -> {
            long offset = consumer.position(key) - recentDriverStatusUpdateTopicRelevantLengthPerPartition;
            consumer.seek(key, offset < 0 ? 0 : offset);
        });
        ConsumerRecords<String, RecentDriverStatusUpdate> poll = consumer.poll(of(100, MILLIS));
        return stream(poll.spliterator(), false)
                .map(ConsumerRecord::value)
                .collect(reverse());
    }

    private <T> Collector<T, ?, Stream<T>> reverse() {
        return Collectors.collectingAndThen(toList(), list -> {
            Collections.reverse(list);
            return list.stream();
        });
    }
}
