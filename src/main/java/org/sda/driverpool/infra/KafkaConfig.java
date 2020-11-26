package org.sda.driverpool.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Getter
@Configuration
@EnableKafka
class KafkaConfig {
    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;
    @Value("${kafka.consumerGroupId}")
    private String consumerGroupId;
    @Value("${kafka.recentDriverStatusUpdateTopic}")
    private String recentDriverStatusUpdateTopic;
    @Value("${kafka.recentDriverStatusUpdateTopicPartitionsNumber:3}")
    private int recentDriverStatusUpdateTopicPartitionsNumber;
    @Value("${kafka.orderPendingDriverEventTopic}")
    private String orderPendingDriverEventTopic;
    @Value("${kafka.orderPendingDriverEventTopicPartitionsNumber:3}")
    private int orderPendingDriverEventTopicPartitionsNumber;
    @Value("${kafka.recentDriverStatusUpdateEventTopic}")
    private String recentDriverStatusUpdateEventTopic;
    @Value("${kafka.recentDriverStatusUpdateEventTopicPartitionsNumber:3}")
    private int recentDriverStatusUpdateEventTopicPartitionsNumber;
}