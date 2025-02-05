package com.bp.common.kafka.config;

import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kafka.config.properties.KafkaConfigurationProperties.TopicConfigurationProperties;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto;
import com.bp.common.kakfa.event.SignUpCertificationMailEvent.SignUpCertificationMailEventProto;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";
    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";
    private static final String TOPIC_CONFIG_NAME_RESERVATION_EVENT = "RESERVATION-REGISTRATION-EVENT";

    private static final String KEY_SPECIFIC_PROTOBUF_VALUE_TYPE = "specific.protobuf.value.type";
    private static final String KEY_SCHEMA_REGISTRY_URL = "schema.registry.url";

    private final KafkaConfigurationProperties kafkaConfigProperties;
    private final KafkaProducerConfig kafkaProducerConfig;

    @Bean("shopLikeEventConsumerConfig")
    public Map<String, Object> shopLikeEventConsumerConfig() {
        final TopicConfigurationProperties shopLikeEventTopicConfig = kafkaConfigProperties.getTopic()
            .get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, shopLikeEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            KEY_SCHEMA_REGISTRY_URL, kafkaConfigProperties.getSchemaRegistryUrl(),
            KEY_SPECIFIC_PROTOBUF_VALUE_TYPE, ShopLikeEventProto.class.getName(),

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            shopLikeEventTopicConfig.getConsumer().getBatchSize()
        );
    }

    @Bean
    public ConsumerFactory<Long, ShopLikeEventProto> shopLikeEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(shopLikeEventConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, ShopLikeEventProto> shopLikeEventListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<Long, ShopLikeEventProto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopLikeEventConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(listenerDefaultErrorHandler(kafkaProducerConfig.shopLikeEventKafkaTemplate()));
        factory.setConcurrency(
            kafkaConfigProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT).getConsumer()
                .getThreadCount());

        return factory;
    }

    @Bean("signUpCertificationMailEventConsumerConfig")
    public Map<String, Object> signUpCertificationMailEventConsumerConfig() {

        final TopicConfigurationProperties signUpCertificationMailEventTopicConfig = kafkaConfigProperties.getTopic()
            .get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, signUpCertificationMailEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            KEY_SCHEMA_REGISTRY_URL, kafkaConfigProperties.getSchemaRegistryUrl(),
            KEY_SPECIFIC_PROTOBUF_VALUE_TYPE, SignUpCertificationMailEventProto.class.getName(),

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG, signUpCertificationMailEventTopicConfig.getConsumer().getBatchSize()
        );
    }

    @Bean
    public ConsumerFactory<String, SignUpCertificationMailEventProto> signUpCertificationMailEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(signUpCertificationMailEventConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEventProto> signUpCertificationMailEventConcurrentKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEventProto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(signUpCertificationMailEventConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(listenerDefaultErrorHandler(kafkaProducerConfig.signUpCertificationMailEventKafkaTemplate()));
        return factory;
    }

    @Bean("reservationEventConsumerConfig")
    public Map<String, Object> reservationEventConsumerConfig() {

        final TopicConfigurationProperties reservationEventTopicConfig = kafkaConfigProperties.getTopic()
            .get(TOPIC_CONFIG_NAME_RESERVATION_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, reservationEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            KEY_SCHEMA_REGISTRY_URL, kafkaConfigProperties.getSchemaRegistryUrl(),
            KEY_SPECIFIC_PROTOBUF_VALUE_TYPE, ReservationEventProto.class.getName(),

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG, reservationEventTopicConfig.getConsumer().getBatchSize()
        );
    }

    @Bean
    public ConsumerFactory<String, ReservationEventProto> reservationEventProtoConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(reservationEventConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationEventProto> reservationEventProtoConcurrentKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, ReservationEventProto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reservationEventProtoConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(listenerDefaultErrorHandler(kafkaProducerConfig.reservationRegistrationEventKafkaTemplate()));
        return factory;
    }

    public DefaultErrorHandler listenerDefaultErrorHandler(final KafkaTemplate<?, ?> kafkaTemplate) {
        // 2초 간격으로 최대 3번 재시도
        final FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3L);
        // DeadLetterPublishingRecoverer 를 사용하여 실패한 메시지를 DLT로 전송
        final DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        // DefaultErrorHandler 에 DLT와 재시도 정책 설정
        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }
}
