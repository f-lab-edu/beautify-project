package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.LikeType;
import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.entity.ShopLike;
import com.beautify_project.bp_mysql.entity.ShopLike.ShopLikeId;
import com.beautify_project.bp_mysql.repository.ShopAdapterRepository;
import com.beautify_project.bp_mysql.repository.ShopLikeAdapterRepository;
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
@Transactional(readOnly = true)
public class ShopLikeEventConsumer {

    private final ShopLikeAdapterRepository shopLikeRepository;
    private final ShopAdapterRepository shopRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].consumer.groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    public void listenShopLikeEvent(final List<ShopLikeEvent> eventsIncludingLikeAndCancel) {
        log.debug("{} counts of event consumed", eventsIncludingLikeAndCancel.size());
        batchShopLikeEvents(eventsIncludingLikeAndCancel);
        batchShopLikeCancelEvents(eventsIncludingLikeAndCancel);
    }

    public void batchShopLikeEvents(final List<ShopLikeEvent> eventsIncludingLikeAndCancel) {

        final List<ShopLikeEvent> likeEvents = filterEventsByLikeType(eventsIncludingLikeAndCancel,
            LikeType.LIKE);

        if (likeEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToIncreaseByShopId = makeCountToUpdateByShopId(likeEvents);

        updateShopLikeCountInShopEntity(countToIncreaseByShopId, LikeType.LIKE);
        bulkInsertShopLikeEntity(likeEvents);
    }

    private void batchShopLikeCancelEvents(final List<ShopLikeEvent> eventsIncludingLikeAndCancel) {

        final List<ShopLikeEvent> cancelEvents = filterEventsByLikeType(eventsIncludingLikeAndCancel,
            LikeType.LIKE_CANCEL);

        if (cancelEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToDecreaseByShopId = makeCountToUpdateByShopId(cancelEvents);

        updateShopLikeCountInShopEntity(countToDecreaseByShopId, LikeType.LIKE_CANCEL);
        removeAllShopLikeEntity(cancelEvents);
    }

    private List<ShopLikeEvent> filterEventsByLikeType(
        final List<ShopLikeEvent> eventsIncludingLikeAndCancel, final LikeType likeType) {

        if (LikeType.LIKE == likeType) {
            final List<ShopLikeEvent> filteredLikeEvents = eventsIncludingLikeAndCancel.stream()
                .filter(event -> event.type() == LikeType.LIKE)
                .toList();

            return filterDuplicated(filteredLikeEvents, likeType);
        }

        final List<ShopLikeEvent> filteredCancelEvents = eventsIncludingLikeAndCancel.stream()
            .filter(event -> event.type() == LikeType.LIKE_CANCEL)
            .toList();

        return filterDuplicated(filteredCancelEvents, likeType);
    }

    private List<ShopLikeEvent> filterDuplicated(final List<ShopLikeEvent> events, final LikeType likeType) {

        final List<ShopLikeId> shopLikeIdsToFind = events.stream()
            .map(event -> ShopLikeId.of(event.shopId(), event.memberEmail()))
            .toList();

        final List<ShopLike> alreadyInsertedShopLikeEntities = shopLikeRepository.findByShopLikeIdIn(
            shopLikeIdsToFind);

        // 이미 db로 처리된 ShopLike
        final Set<ShopLikeId> alreadyInsertedIds = alreadyInsertedShopLikeEntities.stream()
            .map(ShopLike::getId)
            .collect(Collectors.toSet());

        // 이미 처리된 좋아요 == db 상에 존재
        if (LikeType.LIKE == likeType) {
            return events.stream()
                .filter(event -> !alreadyInsertedIds.contains(
                    ShopLikeId.of(event.shopId(), event.memberEmail())))
                .toList();
        }

        // 이미 처리된 좋아요 취소 == db 상에 존재하지 않음
        return events.stream()
            .filter(event -> alreadyInsertedIds.contains(
                ShopLikeId.of(event.shopId(), event.memberEmail())))
            .toList();
    }

    private Map<Long, Integer> makeCountToUpdateByShopId(final List<ShopLikeEvent> filteredEvents) {
        return filteredEvents.stream().collect(
            Collectors.toMap(
                ShopLikeEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );
    }

    private void updateShopLikeCountInShopEntity(final Map<Long, Integer> countToProcessByShopId, final LikeType likeType) {
        final Set<Long> shopIdsToFind = countToProcessByShopId.keySet();
        List<Shop> foundShops = shopRepository.findByIdIn(shopIdsToFind);

        if (LikeType.LIKE == likeType) {
            foundShops.forEach(foundShop -> foundShop.increaseLikeCount(
                countToProcessByShopId.get(foundShop.getId())
            ));
        } else {
            foundShops.forEach(foundShop -> foundShop.decreaseLikeCount(
                countToProcessByShopId.get(foundShop.getId())
            ));
        }
        shopRepository.saveAll(foundShops);
        log.debug("{} counts of Shop entity updated", foundShops.size());
    }

    @Transactional
    private void bulkInsertShopLikeEntity(final List<ShopLikeEvent> events) {
        List<ShopLike> shopLikesToRegister = events.stream()
            .map(event -> ShopLike.of(event.shopId(), event.memberEmail()))
            .toList();
        shopLikeRepository.bulkInsert(shopLikesToRegister);
        log.debug("{} counts of ShopLike entity inserted", shopLikesToRegister.size());
    }

    @Transactional
    private void removeAllShopLikeEntity(final List<ShopLikeEvent> events) {
        final List<ShopLikeId> shopLikeIdsToRemove = events.stream().map(event -> ShopLikeId.of(
            event.shopId(), event.memberEmail())).toList();
        shopLikeRepository.deleteAllByIdInBatch(shopLikeIdsToRemove);
        log.debug("{} counts of ShopLike entity removed", shopLikeIdsToRemove.size());
    }
}
