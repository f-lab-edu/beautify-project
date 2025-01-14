package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_kafka_event_consumer.config.properties.KafkaConsumerConfigProperties;
import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeCancelEvent;
import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeEvent;
import com.beautify_project.bp_kafka_event_consumer.openfeign.ShopLikeApi;
import com.beautify_project.bp_kafka_event_consumer.openfeign.ShopLikeCancelApi;
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
    private final ShopLikeCancelApi shopLikeCancelApi;
    private final KafkaConsumerConfigProperties consumerProperties;

    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    public void listenShopLikeEvent(final List<ShopLikeEvent> events) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.debug("{} counts of event consumed", events.size());
        shopLikeApi.batchLikeShops(events);
    }

    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-CANCEL-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-CANCEL-EVENT'].groupId}",
        containerFactory = "shopLikeCancelEventListenerContainerFactory")
    public void listenShopLikeCancelEvent(final List<ShopLikeCancelEvent> events) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.debug("{} counts of event consumed", events.size());
        shopLikeCancelApi.batchShopLikeCancel(events);
    }
}
