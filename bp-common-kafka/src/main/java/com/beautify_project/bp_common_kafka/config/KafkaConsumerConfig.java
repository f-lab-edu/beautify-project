package com.beautify_project.bp_common_kafka.config;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties.TopicConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
import com.beautify_project.bp_common_kafka.serializer.MessagePackDeserializer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private static final String TRUSTED_PACKAGES = "com.beuatify_project.bp_common.event";
    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";
    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_CANCEL_EVENT = "SHOP-LIKE-CANCEL-EVENT";
    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";

    private final KafkaConfigurationProperties kafkaConfig;

    @Bean("shopLikeEventConsumerConfig")
    public Map<String, Object> shopLikeEventConsumerConfig() {

        final TopicConfigurationProperties shopLikeEventTopicConfig = kafkaConfig.getTopic()
            .get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT);

        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, shopLikeEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessagePackDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            shopLikeEventTopicConfig.getConsumer().getBatchSize(),

            JsonDeserializer.VALUE_DEFAULT_TYPE, ShopLikeEvent.class,
            JsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);
    }

    @Bean(name = "shopLikeEventConsumerFactory")
    public ConsumerFactory<String, ShopLikeEvent> shopLikeEventConsumerFactory() {
        // 들어오는 record 를 객체로 받기 위한 deserializer
        final MessagePackDeserializer<ShopLikeEvent> deserializer = new MessagePackDeserializer<>(
            ShopLikeEvent.class);

        return new DefaultKafkaConsumerFactory<>(shopLikeEventConsumerConfig(), new StringDeserializer(),
            deserializer);
    }

    @Bean(name = "shopLikeEventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent> shopLikeEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopLikeEventConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            if (exception instanceof SerializationException || exception instanceof IllegalStateException) {
                log.error(
                    "Skip event due to deserialization error: topic - {} | partition - {} | value - {}",
                    record.topic(), record.partition(), record.value());
                // TODO: 별도의 큐 처리 또는 추가 로직으로 처리 필요
            }
        }));
        return factory;
    }

    @Bean(name = "signUpCertificationMailEventConsumerConfig")
    public Map<String, Object> signUpCertificationMailEventConsumerConfig() {

        final TopicConfigurationProperties signUpCertificationMailEventTopicConfig = kafkaConfig.getTopic()
            .get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG,signUpCertificationMailEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG, signUpCertificationMailEventTopicConfig.getConsumer().getBatchSize(),

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
