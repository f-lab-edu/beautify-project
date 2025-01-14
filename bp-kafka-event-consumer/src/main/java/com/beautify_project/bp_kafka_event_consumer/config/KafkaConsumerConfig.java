package com.beautify_project.bp_kafka_event_consumer.config;

import com.beautify_project.bp_kafka_event_consumer.config.properties.KafkaConsumerConfigProperties;
import com.beuatify_project.bp_common.event.ShopLikeCancelEvent;
import com.beuatify_project.bp_common.event.ShopLikeEvent;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
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
    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_CANCEL_EVENT = "SHOP-LIKE-CANCEL-EVENT";
    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";

    private final KafkaConsumerConfigProperties configProperties;

    @Bean("shopLikeEventConsumerConfig")
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

    @Bean(name = "shopLikeCancelEventConsumerConfig")
    public Map<String, Object> shopLikeCancelEventConsumerConfig() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBroker(),
            ConsumerConfig.GROUP_ID_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_CANCEL_EVENT).getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_CANCEL_EVENT).getBatchSize(),

            JsonDeserializer.VALUE_DEFAULT_TYPE, ShopLikeCancelEvent.class,
            JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES
        );
    }

    @Bean(name = "shopLikeCancelEventConsumerFactory")
    public ConsumerFactory<String, ShopLikeCancelEvent> shopLikeCancelEventConsumerFactory() {
        // 들어오는 record 를 객체로 받기 위한 deserializer
        final JsonDeserializer<ShopLikeCancelEvent> deserializer = new JsonDeserializer<>(
            ShopLikeCancelEvent.class, false);

        return new DefaultKafkaConsumerFactory<>(shopLikeCancelEventConsumerConfig(),
            new StringDeserializer(), deserializer);
    }

    @Bean(name = "shopLikeCancelEventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShopLikeCancelEvent> shopLikeCancelEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ShopLikeCancelEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopLikeCancelEventConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean(name = "signUpCertificationMailEventConsumerConfig")
    public Map<String, Object> signUpCertificationMailEventConsumerConfig() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBroker(),
            ConsumerConfig.GROUP_ID_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT).getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            // 역직렬화 실패 무한 로그 방지
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            configProperties.getTopic().get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT).getBatchSize(),

            JsonDeserializer.VALUE_DEFAULT_TYPE, SignUpCertificationMailEvent.class,
            JsonDeserializer.TRUSTED_PACKAGES, "com.beautify_project.bp_kafka_event_consumer.event"
        );
    }

    @Bean(name = "signUpCertificationMailEventConsumerFactory")
    public ConsumerFactory<String, SignUpCertificationMailEvent> signUpCertificationMailEventConsumerFactory() {
        // 들어오는 record 를 객체로 받기 위한 deserializer
        final JsonDeserializer<SignUpCertificationMailEvent> deserializer = new JsonDeserializer<>(
            SignUpCertificationMailEvent.class, false);

        return new DefaultKafkaConsumerFactory<>(signUpCertificationMailEventConsumerConfig(),
            new StringDeserializer(), deserializer);
    }

    @Bean(name = "signUpCertificationMailEventConcurrentKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEvent> signUpCertificationMailEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(signUpCertificationMailEventConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
}
