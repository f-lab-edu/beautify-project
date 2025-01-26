package com.bp.app.event.consumer.listener;

import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopLikeEventListener {

    private final ShopLikeAdapterRepository shopLikeRepository;
    private final ShopAdapterRepository shopRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['SHOP-LIKE-EVENT'].consumer.groupId}",
        containerFactory = "shopLikeEventListenerContainerFactory")
    @Transactional
    public void listenShopLikeEvent(final List<ShopLikeEventProto> eventsIncludingLikeAndCancel) {
        log.debug("{} counts of event consumed", eventsIncludingLikeAndCancel.size());
        batchShopLikeEvents(eventsIncludingLikeAndCancel);
        batchShopLikeCancelEvents(eventsIncludingLikeAndCancel);
    }

    public void batchShopLikeEvents(final List<ShopLikeEventProto> eventsIncludingLikeAndCancel) {
        final List<ShopLikeEventProto> validLikeEvents = filterEvents(eventsIncludingLikeAndCancel,
            LikeType.LIKE);

        if (validLikeEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToIncreaseByShopId = makeCountToUpdateByShopId(validLikeEvents);
        updateShopLikeCountInShopEntity(countToIncreaseByShopId, LikeType.LIKE);
        bulkInsertShopLikeEntity(validLikeEvents);
    }

    private List<ShopLikeEventProto> filterEvents(final List<ShopLikeEventProto> allEvents,
        final LikeType likeType) {

        // 1. 좋아요 이벤트만 필터링
        final List<ShopLikeEventProto> likeEvents = filterEventsByLikeType2(allEvents, likeType);

        // 2. DB에 존재하는 Shop 에 대한 요청만 필터링
        final List<ShopLikeEventProto> validShopEvents = filterShopEvents(likeEvents);

        // 3. 이미 처리된 좋아요 필터링
        return filterAlreadyProcessed(validShopEvents, likeType);
    }

    private List<ShopLikeEventProto> filterEventsByLikeType2(
        final List<ShopLikeEventProto> eventsIncludingLikeAndCancel, final LikeType likeType) {

        List<ShopLikeEventProto> filteredEvents;

        if (LikeType.LIKE == likeType) {
            filteredEvents = eventsIncludingLikeAndCancel.stream()
                .filter(event -> event.getType() == LikeType.LIKE)
                .toList();
        } else {
            filteredEvents = eventsIncludingLikeAndCancel.stream()
                .filter(event -> event.getType() == LikeType.LIKE_CANCEL)
                .toList();
        }

        return filteredEvents;
    }

    private List<ShopLikeEventProto> filterShopEvents(final List<ShopLikeEventProto> events) {
        final Set<Long> shopIdsToFind = events.stream()
            .map(ShopLikeEventProto::getShopId)
            .collect(Collectors.toSet());

        final Set<Long> existedShopIds = shopRepository.findByIdIn(shopIdsToFind).stream()
            .map(Shop::getId)
            .collect(Collectors.toSet());

        return events.stream()
            .filter(validEvent -> existedShopIds.contains(validEvent.getShopId()))
            .toList();
    }

    private List<ShopLikeEventProto> filterAlreadyProcessed(final List<ShopLikeEventProto> events,
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

    private void batchShopLikeCancelEvents(
        final List<ShopLikeEventProto> eventsIncludingLikeAndCancel) {

        final List<ShopLikeEventProto> validCancelEvents = filterEvents(
            eventsIncludingLikeAndCancel, LikeType.LIKE_CANCEL);
        if (validCancelEvents.isEmpty()) {
            return;
        }

        final Map<Long, Integer> countToDecreaseByShopId = makeCountToUpdateByShopId(
            validCancelEvents);

        updateShopLikeCountInShopEntity(countToDecreaseByShopId, LikeType.LIKE_CANCEL);
        removeAllShopLikeEntity(validCancelEvents);
    }

    private Map<Long, Integer> makeCountToUpdateByShopId(final List<ShopLikeEventProto> shopLikeEvents) {

        final Set<Long> shopIdsToFind = shopLikeEvents.stream()
            .map(ShopLikeEventProto::getShopId)
            .collect(Collectors.toSet());

        // DB에 존재하는 shopId
        final Set<Long> existedShopIds = shopRepository.findByIdIn(shopIdsToFind).stream()
            .map(Shop::getId)
            .collect(Collectors.toSet());

        return shopLikeEvents.stream()
            .filter(event -> existedShopIds.contains(event.getShopId()))
            .collect(
                Collectors.toMap(ShopLikeEventProto::getShopId,
                    event -> 1,
                    Integer::sum)
            );
    }

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

    private void bulkInsertShopLikeEntity(final List<ShopLikeEventProto> events) {
        List<ShopLike> shopLikesToRegister = events.stream()
            .map(event -> ShopLike.of(event.getShopId(), event.getMemberEmail()))
            .toList();
        shopLikeRepository.bulkInsert(shopLikesToRegister);
        log.debug("{} counts of ShopLike entity inserted", shopLikesToRegister.size());
    }

    private void removeAllShopLikeEntity(final List<ShopLikeEventProto> events) {
        final List<ShopLikeId> shopLikeIdsToRemove = events.stream()
            .map(event -> ShopLikeId.of(event.getShopId(), event.getMemberEmail()))
            .toList();
        shopLikeRepository.deleteAllByIdInBatch(shopLikeIdsToRemove);
        log.debug("{} counts of ShopLike entity removed", shopLikeIdsToRemove.size());
    }
}
