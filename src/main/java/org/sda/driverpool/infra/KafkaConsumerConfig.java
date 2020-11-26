package org.sda.driverpool.infra;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.sda.driverpool.entity.RecentDriverStatusUpdate;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

@Configuration
public class KafkaConsumerConfig extends KafkaConfig {

    @Bean
    KafkaConsumer<String, RecentDriverStatusUpdate> createRecentDriverStatusUpdatesConsumer() {
        Map<String, Object> configs = basicConfig();
        final String STORAGE_GROUP = "storage-group";
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, getConsumerGroupId() + STORAGE_GROUP);
        configs.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
        final KafkaConsumer<String, RecentDriverStatusUpdate> consumer = new KafkaConsumer<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(RecentDriverStatusUpdate.class));
        consumer.subscribe(singletonList(getRecentDriverStatusUpdateTopic()));
        return consumer;
    }

    @Bean
    ConsumerFactory<String, RecentDriverStatusUpdateEvent> recentDriverStatusUpdateEventTopicConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(basicConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(RecentDriverStatusUpdateEvent.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, RecentDriverStatusUpdateEvent> recentDriverStatusUpdateEventTopicListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RecentDriverStatusUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(recentDriverStatusUpdateEventTopicConsumerFactory());
        return factory;
    }

    @Bean
    ConsumerFactory<String, OrderPendingDriverEvent> orderPendingDriverEventTopicConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(basicConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(OrderPendingDriverEvent.class));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderPendingDriverEvent> orderPendingDriverEventTopicListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderPendingDriverEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderPendingDriverEventTopicConsumerFactory());
        return factory;
    }

    private Map<String, Object> basicConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapAddress());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, getConsumerGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
