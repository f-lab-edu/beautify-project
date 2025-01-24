package com.bp.app.event.consumer.listener;

import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import com.bp.domain.mysql.repository.ShopLikeAdapterRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopLikeEventListener {

    private final ShopLikeAdapterRepository shopLikeRepository;
    private final ShopAdapterRepository shopRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].consumer.groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    @Transactional
    public void listenShopLikeEvent(final List<ShopLikeEventProto> eventsIncludingLikeAndCancel,
        @Header(KafkaHeaders.RECEIVED_PARTITION) final List<Integer> partitions) {
        log.debug("{} counts of event consumed", eventsIncludingLikeAndCancel.size());
        batchShopLikeEvents(eventsIncludingLikeAndCancel);
        batchShopLikeCancelEvents(eventsIncludingLikeAndCancel);
    }

    public void batchShopLikeEvents(final List<ShopLikeEventProto> eventsIncludingLikeAndCancel) {

        final List<ShopLikeEventProto> likeEvents = filterEventsByLikeType(
            eventsIncludingLikeAndCancel,
            LikeType.LIKE);

        if (likeEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToIncreaseByShopId = makeCountToUpdateByShopId(likeEvents);

        updateShopLikeCountInShopEntity(countToIncreaseByShopId, LikeType.LIKE);
        bulkInsertShopLikeEntity(likeEvents);
    }

    private void batchShopLikeCancelEvents(
        final List<ShopLikeEventProto> eventsIncludingLikeAndCancel) {

        final List<ShopLikeEventProto> cancelEvents = filterEventsByLikeType(
            eventsIncludingLikeAndCancel,
            LikeType.LIKE_CANCEL);

        if (cancelEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToDecreaseByShopId = makeCountToUpdateByShopId(cancelEvents);

        updateShopLikeCountInShopEntity(countToDecreaseByShopId, LikeType.LIKE_CANCEL);
        removeAllShopLikeEntity(cancelEvents);
    }

    private List<ShopLikeEventProto> filterEventsByLikeType(
        final List<ShopLikeEventProto> eventsIncludingLikeAndCancel, final LikeType likeType) {

        if (LikeType.LIKE == likeType) {
            final List<ShopLikeEventProto> filteredLikeEvents = eventsIncludingLikeAndCancel.stream()
                .filter(event -> event.getType() == LikeType.LIKE)
                .toList();
            return filterDuplicated(filteredLikeEvents, likeType);
        }

        final List<ShopLikeEventProto> filteredCancelEvents = eventsIncludingLikeAndCancel.stream()
            .filter(event -> event.getType() == LikeType.LIKE_CANCEL)
            .toList();

        return filterDuplicated(filteredCancelEvents, likeType);
    }

    private List<ShopLikeEventProto> filterDuplicated(final List<ShopLikeEventProto> events,
        final LikeType likeType) {

        final List<ShopLikeId> shopLikeIdsToFind = events.stream()
            .map(event -> ShopLikeId.of(event.getShopId(), event.getMemberEmail()))
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
                    ShopLikeId.of(event.getShopId(), event.getMemberEmail())))
                .toList();
        }

        // 이미 처리된 좋아요 취소 == db 상에 존재하지 않음
        return events.stream()
            .filter(event -> alreadyInsertedIds.contains(
                ShopLikeId.of(event.getShopId(), event.getMemberEmail())))
            .toList();
    }

    private Map<Long, Integer> makeCountToUpdateByShopId(
        final List<ShopLikeEventProto> filteredEvents) {
        return filteredEvents.stream().collect(
            Collectors.toMap(
                ShopLikeEventProto::getShopId,
                event -> 1,
                Integer::sum
            )
        );
    }

    @Transactional
    private void updateShopLikeCountInShopEntity(final Map<Long, Integer> countToProcessByShopId,
        final LikeType likeType) {
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
    private void bulkInsertShopLikeEntity(final List<ShopLikeEventProto> events) {
        List<ShopLike> shopLikesToRegister = events.stream()
            .map(event -> ShopLike.of(event.getShopId(), event.getMemberEmail()))
            .toList();
        shopLikeRepository.bulkInsert(shopLikesToRegister);
        log.debug("{} counts of ShopLike entity inserted", shopLikesToRegister.size());
    }

    @Transactional
    private void removeAllShopLikeEntity(final List<ShopLikeEventProto> events) {
        final List<ShopLikeId> shopLikeIdsToRemove = events.stream()
            .map(event -> ShopLikeId.of(event.getShopId(), event.getMemberEmail()))
            .toList();
        shopLikeRepository.deleteAllByIdInBatch(shopLikeIdsToRemove);
        log.debug("{} counts of ShopLike entity removed", shopLikeIdsToRemove.size());
    }
}
