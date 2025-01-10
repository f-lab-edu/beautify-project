package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.event.ShopLikeCancelEvent;
import com.beautify_project.bp_app_api.dto.event.ShopLikeEvent;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.entity.ShopFacility;
import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.entity.ShopOperation;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.producer.KafkaEventProducer;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import com.beautify_project.bp_app_api.utils.Validator;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopOperationService shopOperationService;
    private final OperationService operationService;
    private final ShopFacilityService shopFacilityService;
    private final FacilityService facilityService;
    private final ShopLikeService shopLikeService;
    private final ShopCategoryService shopCategoryService;
    private final ImageService imageService;
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerShop(final ShopRegistrationRequest shopRegistrationRequest) {

        final Shop registeredShop = shopRepository.save(Shop.from(shopRegistrationRequest));
        final String registeredShopId = registeredShop.getId();
        final List<String> operationIds = shopRegistrationRequest.operationIds();
        final List<String> facilityIds = shopRegistrationRequest.facilityIds();

        // 시술 ID가 포함되어 있을 경우에만 샵에 포함된 시술과 카테고리로 insert
        if (!Validator.isNullOrEmpty(operationIds)) {
            shopOperationService.registerShopOperations(registeredShopId, operationIds);
            shopCategoryService.registerShopCategories(registeredShopId, operationIds);
        }

        // 편의시설 ID가 포함되어 있을 경우에만 샵에 포함된 편의시설로 insert
        if (!Validator.isNullOrEmpty(facilityIds)) {
            shopFacilityService.registerShopFacilities(registeredShopId, facilityIds);
        }

        log.debug("Registered Shop: {}", registeredShop);

        return ResponseMessage.createResponseMessage(new ShopRegistrationResult(registeredShopId));
    }

    public ResponseMessage findShopList(final ShopListFindRequestParameters parameters) {
        // TODO: jpql 조인 쿼리로 개선 필요
        final Pageable pageable = PageRequest.of(parameters.page(), parameters.count(),
            Sort.by(Sort.Direction.fromString(parameters.orderType().name()),
                parameters.searchType().getEntityName()));

        final List<Shop> foundShops = shopRepository.findAll(pageable).getContent();
        return ResponseMessage.createResponseMessage(createShopListFindResults(foundShops));
    }

    private List<ShopListFindResult> createShopListFindResults(final List<Shop> foundShops) {

        final List<String> shopIds = foundShops.stream().map(Shop::getId).toList();
        final Map<String, List<String>> operationNamesByShopId = findOperationNamesByShops(shopIds);
        final Map<String, List<String>> facilityNamesByShopId = findFacilityNamesByShops(shopIds);

        return foundShops.stream().map(foundShop -> {
                final String shopId = foundShop.getId();
                final List<String> operationNames = operationNamesByShopId.get(shopId);
                final List<String> facilityNames = facilityNamesByShopId.get(shopId);
                final String thumbnailLink = imageService.issuePreSignedGetUrl(
                    foundShop.getImageFileIds().get(0));
                return ShopListFindResult.createShopListFindResult(foundShop, operationNames,
                    facilityNames, thumbnailLink);
            })
            .collect(Collectors.toList());
    }

    private Map<String, List<String>> findOperationNamesByShops(final List<String> shopIds) {
        final List<ShopOperation> foundShopOperations = shopOperationService.findShopOperationsByShopIds(
            shopIds);

        final Map<String, List<String>> operationIdsByShopId = foundShopOperations.stream()
            .collect(Collectors.groupingBy(shopOperation -> shopOperation.getId().getShopId(),
                Collectors.mapping(shopOperation -> shopOperation.getId().getOperationId(),
                    Collectors.toList())));

        return operationIdsByShopId.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> operationService.findOperationsByIds(entry.getValue())
                    .stream().map(Operation::getName).collect(Collectors.toList())));
    }

    private Map<String, List<String>> findFacilityNamesByShops(final List<String> shopIds) {
        final List<ShopFacility> foundShopFacilities = shopFacilityService.findShopFacilitiesByShopIds(
            shopIds);

        final Map<String, List<String>> facilityIdsByShopId = foundShopFacilities.stream()
            .collect(Collectors.groupingBy(shopFacility -> shopFacility.getId().getShopId(),
                Collectors.mapping(shopFacility -> shopFacility.getId().getFacilityId(),
                    Collectors.toList())));

        return facilityIdsByShopId.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                facilityService.findFacilitiesByIds(entry.getValue()).stream()
                    .map(Facility::getName).collect(Collectors.toList())));
    }

    public Shop findShopById(final @NotBlank String shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SH001));
    }

    @Async(value = "ioBoundExecutor")
    public void produceShopLikeEvent(final String shopId, final String memberEmail) {
        kafkaEventProducer.publishShopLikeEvent(new ShopLikeEvent(shopId, memberEmail));
    }

    @Async(value = "ioBoundExecutor")
    public void produceShopLikeCancelEvent(final String shopId, final String memberEmail) {
        kafkaEventProducer.publishShopLikeCancelEvent(new ShopLikeCancelEvent(shopId, memberEmail));
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchShopLikes(final List<ShopLikeEvent> shopLikeEvents) {
        final Map<String, Integer> countToIncreaseByShopId = shopLikeEvents.stream().collect(
            Collectors.toMap(
                ShopLikeEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );

        final Set<String> shopIds = countToIncreaseByShopId.keySet();
        final List<Shop> foundShops = shopRepository.findByIdIn(shopIds);

        foundShops.forEach(foundShop -> foundShop.increaseLikeCount(
            countToIncreaseByShopId.get(foundShop.getId())));
        log.info("{} counts of shops save all called", foundShops.size());
        shopRepository.saveAll(foundShops);

        List<ShopLike> shopLikesToRegister = shopLikeEvents.stream()
            .map(event -> ShopLike.of(event.shopId(), event.memberEmail())).toList();
        log.info("{} counts of shopLikes save all called", shopLikesToRegister.size());

        shopLikeService.saveAllShopLikes(shopLikesToRegister);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchShopLikesCancel(final List<ShopLikeCancelEvent> shopLikeEventCancels) {
        final Map<String, Integer> countToDecreaseByShopId = shopLikeEventCancels.stream().collect(
            Collectors.toMap(
                ShopLikeCancelEvent::shopId,
                event -> 1,
                Integer::sum
            )
        );

        final Set<String> shopIds = countToDecreaseByShopId.keySet();
        final List<Shop> foundShops = shopRepository.findByIdIn(shopIds);

        foundShops.forEach(foundShop -> foundShop.decreaseLikeCount(
            countToDecreaseByShopId.get(foundShop.getId())));
        shopRepository.saveAll(foundShops);

        List<ShopLike> shopLikesToDelete = shopLikeEventCancels.stream()
            .map(event -> ShopLike.of(event.shopId(), event.memberEmail())).toList();
        shopLikeService.deleteAllShopLikes(shopLikesToDelete);
    }
}
