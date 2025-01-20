package com.beautify_project.bp_common_kafka.config;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties.TopicConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent.SignUpCertificationMailEventProto;
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

    private static final String KEY_SPECIFIC_PROTOBUF_VALUE_TYPE = "specific.protobuf.value.type";
    private static final String KEY_SCHEMA_REGISTRY_URL = "schema.registry.url";

    private final KafkaConfigurationProperties kafkaConfig;
    private final KafkaProducerConfig kafkaProducerConfig;

    @Bean("shopLikeEventConsumerConfig")
    public Map<String, Object> shopLikeEventConsumerConfig() {
        final TopicConfigurationProperties shopLikeEventTopicConfig = kafkaConfig.getTopic()
            .get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, shopLikeEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            KEY_SCHEMA_REGISTRY_URL, kafkaConfig.getSchemaRegistryUrl(),
            KEY_SPECIFIC_PROTOBUF_VALUE_TYPE, ShopLikeEventProto.class.getName(),

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
            shopLikeEventTopicConfig.getConsumer().getBatchSize()
        );
    }

    @Bean
    public ConsumerFactory<String, ShopLikeEvent.ShopLikeEventProto> shopLikeEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(shopLikeEventConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent.ShopLikeEventProto> shopLikeEventListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, ShopLikeEvent.ShopLikeEventProto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(shopLikeEventConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(defaultErrorHandler());

        return factory;
    }

    @Bean("signUpCertificationMailEventConsumerConfig")
    public Map<String, Object> signUpCertificationMailEventConsumerConfig() {

        final TopicConfigurationProperties signUpCertificationMailEventTopicConfig = kafkaConfig.getTopic()
            .get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT);

        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ConsumerConfig.GROUP_ID_CONFIG, signUpCertificationMailEventTopicConfig.getConsumer().getGroupId(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class,

            KEY_SCHEMA_REGISTRY_URL, kafkaConfig.getSchemaRegistryUrl(),
            KEY_SPECIFIC_PROTOBUF_VALUE_TYPE, SignUpCertificationMailEventProto.class.getName(),

            ConsumerConfig.MAX_POLL_RECORDS_CONFIG, signUpCertificationMailEventTopicConfig.getConsumer().getBatchSize()
        );
    }

    @Bean
    public ConsumerFactory<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(signUpCertificationMailEventConsumerConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventConcurrentKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(signUpCertificationMailEventConsumerFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(defaultErrorHandler());

        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        // 2초 간격으로 최대 3번 재시도
        final FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3L);
        // DeadLetterPublishingRecoverer 를 사용하여 실패한 메시지를 DLT로 전송
        final DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaProducerConfig.protobufErrorKafkaTemplate());
        // DefaultErrorHandler 에 DLT와 재시도 정책 설정
        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }
}
