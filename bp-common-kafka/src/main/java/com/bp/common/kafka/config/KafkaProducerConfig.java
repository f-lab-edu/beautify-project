package com.bp.common.kafka.config;

import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kafka.partitioner.ShopLikeEventCustomPartitioner;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto;
import com.bp.common.kakfa.event.SignUpCertificationMailEvent;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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

    public Map<String, Object> commonProtobufProducerConfigProps() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBrokerUrl());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class);
        props.put(SCHEMA_REGISTRY_URL_KEY, kafkaConfig.getSchemaRegistryUrl());

        return props;
    }

    public Map<String, Object> shopLikeEventProducerConfigProps() {
        final Map<String, Object> protobufProps = commonProtobufProducerConfigProps();
        protobufProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        protobufProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, ShopLikeEventCustomPartitioner.class);
        return protobufProps;
    }

    @Bean
    public ProducerFactory<Long, ShopLikeEventProto> shopLikeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(shopLikeEventProducerConfigProps());
    }

    @Bean
    public KafkaTemplate<Long, ShopLikeEventProto> shopLikeEventKafkaTemplate() {
        return new KafkaTemplate<>(shopLikeEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(commonProtobufProducerConfigProps());
    }

    @Bean
    public KafkaTemplate<String, SignUpCertificationMailEvent.SignUpCertificationMailEventProto> signUpCertificationMailEventKafkaTemplate() {
        return new KafkaTemplate<>(signUpCertificationMailEventProducerFactory());
    }
}
