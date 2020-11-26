package org.sda.driverpool.infra;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig extends KafkaConfig {

    @Bean
    public NewTopic recentDriverStatusUpdateTopic() {
        return new NewTopic(getRecentDriverStatusUpdateTopic(), getRecentDriverStatusUpdateTopicPartitionsNumber(), (short) 2);
    }

    @Bean
    public NewTopic recentDriverStatusUpdateEventTopic() {
        return new NewTopic(getRecentDriverStatusUpdateEventTopic(), getRecentDriverStatusUpdateEventTopicPartitionsNumber(), (short) 2);
    }

    @Bean
    public NewTopic orderPendingDriverEventTopic() {
        return new NewTopic(getOrderPendingDriverEventTopic(), getOrderPendingDriverEventTopicPartitionsNumber(), (short) 2);
    }
}