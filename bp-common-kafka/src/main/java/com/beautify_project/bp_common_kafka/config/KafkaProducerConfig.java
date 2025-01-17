package com.beautify_project.bp_common_kafka.config;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeCancelEvent;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
import com.beautify_project.bp_common_kafka.serializer.MessagePackSerializer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigurationProperties kafkaConfig;

    @Bean
    public Map<String, Object> producerConfig() {
        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessagePackSerializer.class
        );
    }

    @Bean(name = "ShopLikeEventProducerFactory")
    public ProducerFactory<String, ShopLikeEvent> shopLikeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean(name = "ShopLikeEventKafkaTemplate")
    public KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate() {
        return new KafkaTemplate<>(shopLikeEventProducerFactory());
    }

    @Bean(name = "ShopLikeCancelEventProducerFactory")
    public ProducerFactory<String, ShopLikeCancelEvent> shopLikeCancelEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean(name = "ShopLikeCancelEventKafkaTemplate")
    public KafkaTemplate<String, ShopLikeCancelEvent> shopLikeCancelEventKafkaTemplate() {
        return new KafkaTemplate<>(shopLikeCancelEventProducerFactory());
    }

    @Bean(name = "SignUpCertificationMailEventProducerFactory")
    public ProducerFactory<String, SignUpCertificationMailEvent> signUpCertificationMailEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean(name = "SignUpCertificationMailEventKafkaTemplate")
    public KafkaTemplate<String, SignUpCertificationMailEvent> signUpCertificationMailEventKafkaTemplate() {
        return new KafkaTemplate<>(signUpCertificationMailEventProducerFactory());
    }
}
