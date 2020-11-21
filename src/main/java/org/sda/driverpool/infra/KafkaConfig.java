package org.sda.driverpool.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Getter
@Configuration
@EnableKafka
class KafkaConfig {
    private static final String BOOTSTRAP_SERVERS_CONFIG = "localhost:9092";
    @Value("${kafka.bootstrapAddress}")
    private String bootstrapAddress;
    @Value("${kafka.consumerGroupId}")
    private String consumerGroupId;
}