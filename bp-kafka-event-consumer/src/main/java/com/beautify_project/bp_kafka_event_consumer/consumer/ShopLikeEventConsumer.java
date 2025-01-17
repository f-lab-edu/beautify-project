package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.entity.ShopLike;
import com.beautify_project.bp_mysql.entity.ShopLike.ShopLikeId;
import com.beautify_project.bp_mysql.repository.ShopAdapterRepository;
import com.beautify_project.bp_mysql.repository.ShopLikeAdapterRepository;
import com.beautify_project.bp_common_kafka.event.ShopLikeCancelEvent;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
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

    private static final String EVENT_LIKE = "like";
    private static final String EVENT_LIKE_CANCEL = "cancel";

    private final ShopLikeAdapterRepository shopLikeAdapterRepository;
    private final ShopAdapterRepository shopAdapterRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].consumer.groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    public void listenShopLikeEvent(final List<ShopLikeEvent> events) {
        log.debug("{} counts of event consumed", events.size());
        batchShopLikeEvents(events);
    }

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-CANCEL-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-CANCEL-EVENT'].consumer.groupId}",
        containerFactory = "shopLikeCancelEventListenerContainerFactory")
    public void listenShopLikeCancelEvent(final List<ShopLikeCancelEvent> events) {
        log.debug("{} counts of event consumed", events.size());
        batchShopLikeCancelEvents(events);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchShopLikeEvents(final List<ShopLikeEvent> events) {
        final List<ShopLikeEvent> filteredEvents = filterAlreadyShopLikePushed(events);
        final Map<Long, Integer> countToIncreaseByShopId = filteredEvents.stream().collect(
            Collectors.toMap(
                ShopLikeEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );

        if (countToIncreaseByShopId.isEmpty()) {
            log.debug("No need to process");
            return;
        }
        updateShopLikeCountInShopEntity(countToIncreaseByShopId, EVENT_LIKE);
        bulkInsertShopLikeEntity(filteredEvents);
    }

    private List<ShopLikeEvent> filterAlreadyShopLikePushed(final List<ShopLikeEvent> events) {
        final List<ShopLikeId> shopLikeIdsToFind = events.stream()
            .map(event -> ShopLikeId.of(event.shopId(), event.memberEmail())).toList();
        final List<ShopLike> alreadyProcessedShopLikes = shopLikeAdapterRepository.findByShopLikeIdIn(
            shopLikeIdsToFind);

        // 중복 제거 로직
        final Set<ShopLikeId> alreadyProcessedIds = alreadyProcessedShopLikes.stream()
            .map(ShopLike::getId)
            .collect(Collectors.toSet());

        return events.stream()
            .filter(event -> !alreadyProcessedIds.contains(ShopLikeId.of(event.shopId(), event.memberEmail())))
            .toList();
    }

    private void updateShopLikeCountInShopEntity(final Map<Long, Integer> countToProcessByShopId, final String eventType) {
        final Set<Long> shopIdsToFind = countToProcessByShopId.keySet();
        List<Shop> foundShops = shopAdapterRepository.findByIdIn(shopIdsToFind);

        if (EVENT_LIKE_CANCEL.equals(eventType)) {
            foundShops.forEach(foundShop -> foundShop.decreaseLikeCount(
                countToProcessByShopId.get(foundShop.getId())
            ));
        } else {
            foundShops.forEach(foundShop -> foundShop.increaseLikeCount(
                countToProcessByShopId.get(foundShop.getId())
            ));
        }

        shopAdapterRepository.saveAll(foundShops);
        log.debug("{} counts of Shop entity updated", foundShops.size());
    }

    @Transactional
    private void bulkInsertShopLikeEntity(final List<ShopLikeEvent> events) {
        List<ShopLike> shopLikesToRegister = events.stream()
            .map(event -> ShopLike.of(event.shopId(), event.memberEmail()))
            .toList();
        shopLikeAdapterRepository.bulkInsert(shopLikesToRegister);
        log.debug("{} counts of ShopLike entity inserted", shopLikesToRegister.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchShopLikeCancelEvents(final List<ShopLikeCancelEvent> events) {
        final List<ShopLikeCancelEvent> filteredEvents = filterAlreadyShopLikeNotPushed(events);
        final Map<Long, Integer> countToDecreaseByShopId = filteredEvents.stream().collect(
            Collectors.toMap(
                ShopLikeCancelEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );

        if (countToDecreaseByShopId.isEmpty()) {
            log.debug("No need to process");
            return;
        }
        updateShopLikeCountInShopEntity(countToDecreaseByShopId, EVENT_LIKE_CANCEL);
        removeAllShopLikeEntity(filteredEvents);
    }

    @Transactional
    private void removeAllShopLikeEntity(final List<ShopLikeCancelEvent> events) {
        final List<ShopLikeId> shopLikeIdsToRemove = events.stream().map(event -> ShopLikeId.of(
            event.shopId(), event.memberEmail())).toList();
        shopLikeAdapterRepository.deleteAllByIdInBatch(shopLikeIdsToRemove);
        log.debug("{} counts of ShopLike entity removed", shopLikeIdsToRemove.size());
    }

    private Map<Long, Integer> makeCountToDecreaseByShopIdExceptAlreadyShopLikePushed(
        final List<ShopLikeCancelEvent> events) {
        final List<ShopLikeCancelEvent> filteredEvents = filterAlreadyShopLikeNotPushed(events);
        return filteredEvents.stream().collect(
            Collectors.toMap(
                ShopLikeCancelEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );
    }

    private List<ShopLikeCancelEvent> filterAlreadyShopLikeNotPushed(
        final List<ShopLikeCancelEvent> events) {
        final List<ShopLikeId> shopLikeIdsToFind = events.stream()
            .map(event -> ShopLikeId.of(event.shopId(), event.memberEmail())).toList();
        final List<ShopLike> alreadyProcessedShopLikes = shopLikeAdapterRepository.findByShopLikeIdIn(
            shopLikeIdsToFind);

        // 중복 제거 로직
        final Set<ShopLikeId> alreadyProcessedIds = alreadyProcessedShopLikes.stream()
            .map(ShopLike::getId)
            .collect(Collectors.toSet());

        return events.stream()
            .filter(event ->
                alreadyProcessedIds.contains(ShopLikeId.of(event.shopId(), event.memberEmail())))
            .toList();
    }
}
