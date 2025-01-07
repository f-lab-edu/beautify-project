package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeCancelEvent;
import com.beautify_project.bp_kafka_event_consumer.openfeign.ShopLikeCancelApi;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopLikeCancelEventConsumer {

    private final ShopLikeCancelApi shopLikeCancelApi;

    @KafkaListener(
        topics = "shop-like-cancel",
        groupId = "shop-like-cancel-event-consumer-group",
        containerFactory = "shopLikeCancelEventListenerContainerFactory")
    public void listenShopLikeCancelEvent(final List<ShopLikeCancelEvent> events) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.debug("event consumed: {}", events.toString());
       shopLikeCancelApi.batchShopLikeCancel(events);
    }

}
