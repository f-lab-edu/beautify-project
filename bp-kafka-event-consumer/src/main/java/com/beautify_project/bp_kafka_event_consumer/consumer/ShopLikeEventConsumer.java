package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_kafka_event_consumer.config.properties.KafkaConsumerConfigProperties;
import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeEvent;
import com.beautify_project.bp_kafka_event_consumer.openfeign.ShopLikeApi;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopLikeEventConsumer {

    private final ShopLikeApi shopLikeApi;
    private final KafkaConsumerConfigProperties consumerProperties;

    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    public void listenShopLikeEvent(final List<ShopLikeEvent> event) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.info("{} counts of event consumed", event.size());
        log.debug("event consumed: {}", event.toString());
        shopLikeApi.batchLikeShops(event);
    }
}
