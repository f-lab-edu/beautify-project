package com.beautify_project.bp_common_kafka.config;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
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

    private static final String SCHEMA_REGISTRY_URL_KEY = "schema.registry.url";

    private final KafkaConfigurationProperties kafkaConfig;

    @Bean
    public Map<String, Object> protoBufProducerConfig() {
        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class.getName(),
            SCHEMA_REGISTRY_URL_KEY,  kafkaConfig.getSchemaRegistryUrl()
        );
    }

    @Bean
    public ProducerFactory<String, ShopLikeEvent.ShopLikeEventProto> shopLikeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(protoBufProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, ShopLikeEvent.ShopLikeEventProto> shopLikeEventKafkaTemplate() {
        return new KafkaTemplate<>(shopLikeEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(protoBufProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventKafkaTemplate() {
        return new KafkaTemplate<>(signUpCertificationMailEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> commonProtobufErrorProducerFactory() {
        return new DefaultKafkaProducerFactory<>(protoBufProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> protobufErrorKafkaTemplate() {
        return new KafkaTemplate<>(commonProtobufErrorProducerFactory());
    }

}
