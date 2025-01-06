package com.beautify_project.bp_app_api.producer;

import com.beautify_project.bp_app_api.config.properties.KafkaProducerConfigProperties;
import com.beautify_project.bp_app_api.dto.event.ShopLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate;
    private final KafkaProducerConfigProperties configProperties;

    public void publishShopLikeEvent(final ShopLikeEvent event) {
        shopLikeEventKafkaTemplate.send(configProperties.getTopic().getShopLikeEvent(), event);
    }

}
