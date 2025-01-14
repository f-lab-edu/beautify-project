package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_mysql.entity.adapter.ShopAdapter;
import com.beautify_project.bp_mysql.entity.adapter.ShopLikeAdapter;
import com.beautify_project.bp_mysql.repository.ShopAdapterRepository;
import com.beautify_project.bp_mysql.repository.ShopLikeAdapterRepository;
import com.beuatify_project.bp_common.event.ShopLikeCancelEvent;
import com.beuatify_project.bp_common.event.ShopLikeEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopLikeEventConsumer {

    private final ShopLikeAdapterRepository shopLikeAdapterRepository;
    private final ShopAdapterRepository shopAdapterRepository;


    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-EVENT'].groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    public void listenShopLikeEvent(final List<ShopLikeEvent> events) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.debug("{} counts of event consumed", events.size());
        batchShopLikes(events);
    }

    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-CANCEL-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['SHOP-LIKE-CANCEL-EVENT'].groupId}",
        containerFactory = "shopLikeCancelEventListenerContainerFactory")
    public void listenShopLikeCancelEvent(final List<ShopLikeCancelEvent> events) {
        // TODO: consume 은 성공하였으나 외부 api 호출 실패시에 대한 예외 처리 추가 필요
        log.debug("{} counts of event consumed", events.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchShopLikes(final List<ShopLikeEvent> events) {
        final Map<Long, Integer> countToIncreaseByShopId = events.stream().collect(
            Collectors.toMap(
                ShopLikeEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );

        final Set<Long> shopIdsToFind = countToIncreaseByShopId.keySet();
        final List<ShopAdapter> foundShops = shopAdapterRepository.findByIdIn(shopIdsToFind);

        foundShops.forEach(foundShop -> foundShop.increaseLikeCount(
            countToIncreaseByShopId.get(foundShop.getId())));
        shopAdapterRepository.saveAll(foundShops);
        log.debug("{} counts of Shop entity updated", foundShops.size());

        List<ShopLikeAdapter> shopLikesToRegister = events.stream()
            .map(event -> ShopLikeAdapter.of(event.shopId(), event.memberEmail(),
                System.currentTimeMillis())).toList();
        shopLikeAdapterRepository.bulkInsert(shopLikesToRegister);
        log.debug("{} counts of ShopLike entity inserted", shopLikesToRegister.size());
    }


}
