package com.beautify_project.bp_app_api.producer;

import static org.junit.jupiter.api.Assertions.*;

import com.beautify_project.bp_app_api.dto.event.ShopLikeEvent;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@EmbeddedKafka
class KafkaEventProducerTest {

    @Autowired
    private KafkaEventProducer producer;

    @Test
    public void publishShopLikeEvent() {
        producer.publishShopLikeEvent(
            new ShopLikeEvent(UUIDGenerator.generate(), "dev.sssukho@gmail.com"));
    }
}
