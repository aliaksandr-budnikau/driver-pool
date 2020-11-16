package org.sda.driverpool.infra;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.sda.driverpool.event.OrderPendingDriverEvent;
import org.sda.driverpool.event.RecentDriverStatusUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig extends KafkaConfig {

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
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapAddress());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, getConsumerGroupId());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
