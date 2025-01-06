package com.beautify_project.bp_kafka_event_consumer.config;

import com.beautify_project.bp_kafka_event_consumer.config.properties.KafkaConsumerConfigProperties;
import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private static final String TRUSTED_PACKAGES = "com.beautify_project.bp_kafka_event_consumer.event";
    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";

    private final KafkaConsumerConfigProperties configProperties;

    @Bean
    public Map<String, Object> shopLikeEventConsumerConfig() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBroker(),
            ConsumerConfig.GROUP_ID_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT).getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT).getBatchSize(),

            JsonDeserializer.VALUE_DEFAULT_TYPE, ShopLikeEvent.class,
            JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES
        );
    }

    @Bean(name = "shopLikeEventConsumerFactory")
    public ConsumerFactory<String, ShopLikeEvent> shopLikeEventConsumerFactory() {

        // 들어오는 record 를 객체로 받기 위한 deserializer
        final JsonDeserializer<ShopLikeEvent> deserializer = new JsonDeserializer<>(
            ShopLikeEvent.class, false);

        return new DefaultKafkaConsumerFactory<>(shopLikeEventConsumerConfig(), new StringDeserializer(),
            deserializer);
    }

    @Bean(name = "shopLikeEventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent> shopLikeEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopLikeEventConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

}
