package com.beautify_project.bp_app_api.config;

import com.beautify_project.bp_app_api.config.properties.KafkaProducerConfigProperties;
import com.beautify_project.bp_app_api.dto.event.ShopLikeEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerConfigProperties configProperties;

    @Bean
    public Map<String, Object> producerConfig() {
        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBroker(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    }

    @Bean(name = "ShopLikeEventProducerFactory")
    public ProducerFactory<String, ShopLikeEvent> shopLikeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean(name = "ShopLikeEventKafkaTemplate")
    public KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate() {
        return new KafkaTemplate<>(shopLikeEventProducerFactory());
    }
}
