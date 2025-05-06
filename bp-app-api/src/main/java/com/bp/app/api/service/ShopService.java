package com.bp.app.api.service;

import com.bp.app.api.config.IOBoundAsyncThreadPoolConfiguration;
import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.provider.image.ImageProvider;
import com.bp.app.api.request.shop.ShopListFindRequestParameters;
import com.bp.app.api.request.shop.ShopRegistrationRequest;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.shop.ShopListFindResult;
import com.bp.app.api.response.shop.ShopRegistrationResult;
import com.bp.domain.mysql.entity.Facility;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.ShopCategory;
import com.bp.domain.mysql.entity.ShopFacility;
import com.bp.domain.mysql.entity.ShopOperation;
import com.bp.domain.mysql.entity.embedded.Address;
import com.bp.domain.mysql.entity.embedded.BusinessTime;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import com.bp.utils.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopService {

    private final ShopAdapterRepository shopAdapterRepository;
    private final ShopOperationService shopOperationService;
    private final OperationService operationService;
    private final ShopFacilityService shopFacilityService;
    private final FacilityService facilityService;
    private final ShopCategoryService shopCategoryService;
    private final ImageProvider imageProvider;
    private final IOBoundAsyncThreadPoolConfiguration ioBoundAsyncThreadPoolConfig;
    private final ShopLikeService shopLikeService;

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerShop(final ShopRegistrationRequest shopRegistrationRequest) {

        final Shop registeredShop = shopAdapterRepository.save(
            createShopEntityFromShopRegistrationRequest(shopRegistrationRequest));
        final Long registeredShopId = registeredShop.getId();
        final List<Long> operationIds = shopRegistrationRequest.operationIds();
        final List<Long> facilityIds = shopRegistrationRequest.facilityIds();

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

    public static Shop createShopEntityFromShopRegistrationRequest(
        final ShopRegistrationRequest request) {

        return Shop.newShop(request.name(), request.contact(), request.url(),
            request.introduction(), request.imageFileIds(),
            Address.of(request.address().dongCode(), request.address().siDoName(),
                request.address().siGoonGooName(), request.address().eubMyunDongName(),
                request.address().roadNameCode(), request.address().roadName(),
                request.address().underGround(), request.address().roadMainNum(),
                request.address().roadSubNum(), request.address().siGoonGooBuildingName(),
                request.address().zipCode(), request.address().apartComplex(),
                request.address().eubMyunDongSerialNumber(), request.address().latitude(),
                request.address().longitude()),
            BusinessTime.of(request.businessTime().openTime(), request.businessTime().closeTime(),
                request.businessTime().breakBeginTime(), request.businessTime().breakEndTime(),
                request.businessTime().offDayOfWeek()));
    }

    public ResponseMessage registerShopAsync(final ShopRegistrationRequest request) {
        final List<CompletableFuture<?>> completableFutures = new ArrayList<>();

        final Shop shopToSave = createShopEntityFromShopRegistrationRequest(request);
        final CompletableFuture<Shop> saveShopAsyncResult = CompletableFuture.supplyAsync(
            () -> shopAdapterRepository.save(shopToSave),
            ioBoundAsyncThreadPoolConfig.getAsyncExecutor());
        completableFutures.add(saveShopAsyncResult);

        // 시술 ID가 포함되어 있을 경우에만 샵에 포함된 시술과 카테고리로 insert
        registerShopOperationAndShopCategoriesAsyncIfOperationIdsExist(request.operationIds(),
            shopToSave, completableFutures);
        // 편의시설 ID가 포함되어 있을 경우에만 샵에 포함된 편의시설로 insert
        registerShopFacilityAsyncIfFacilityIdsExist(request.facilityIds(), shopToSave, completableFutures);

        // 모든 비동기 작업 완료는 확인 필요
        try {
            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
            return ResponseMessage.createResponseMessage(new ShopRegistrationResult(
                shopToSave.getId()));
        } catch (CompletionException exception) {
            rollbackAll(completableFutures);
            throw new BpCustomException(ErrorCode.SH002);
        }
    }

    private void registerShopOperationAndShopCategoriesAsyncIfOperationIdsExist(final List<Long> operationIds,
        final Shop shopToSave, final List<CompletableFuture<?>> completableFutures) {

        if (Validator.isNullOrEmpty(operationIds)) {
            return;
        }

        final CompletableFuture<List<ShopOperation>> saveShopOperationsResult = CompletableFuture.supplyAsync(
            () -> shopOperationService.registerShopOperations(shopToSave.getId(), operationIds),
            ioBoundAsyncThreadPoolConfig.getAsyncExecutor());
        final CompletableFuture<List<ShopCategory>> saveShopCategoriesResult = CompletableFuture.supplyAsync(
            () -> shopCategoryService.registerShopCategories(shopToSave.getId(), operationIds),
            ioBoundAsyncThreadPoolConfig.getAsyncExecutor());

        completableFutures.add(saveShopOperationsResult);
        completableFutures.add(saveShopCategoriesResult);
    }

    private void registerShopFacilityAsyncIfFacilityIdsExist(final List<Long> facilityIds,
        final Shop shopToSave, final List<CompletableFuture<?>> completableFutures) {

        if (Validator.isNullOrEmpty(facilityIds)) {
            return;
        }

        final CompletableFuture<List<ShopFacility>> saveShopFacilitiesResult = CompletableFuture.supplyAsync(
            () -> shopFacilityService.registerShopFacilities(shopToSave.getId(), facilityIds),
            ioBoundAsyncThreadPoolConfig.getAsyncExecutor());
        completableFutures.add(saveShopFacilitiesResult);
    }

    private void rollbackAll(final List<CompletableFuture<?>> completableFutures) {
        for (CompletableFuture<?> completableFuture : completableFutures) {
            try {
                Object object = completableFuture.join();
                if (object instanceof Shop) {
                    shopAdapterRepository.delete((Shop) object);
                } else if (object instanceof ShopOperation) {
                    shopOperationService.remove((ShopOperation) object);
                } else if (object instanceof ShopCategory) {
                    shopCategoryService.delete((ShopCategory) object);
                } else {
                    shopFacilityService.delete((ShopFacility) object);
                }
            } catch (Exception exception) {
                log.error("Failed to rollback: {}", completableFuture.join(), exception);
            }
        }
    }

    @Cacheable(
        value = "shopList",
        key = "#a0.searchType",
        condition = "#a0.searchType == T(com.bp.app.api.enumeration.ShopSearchType).LOCATION"
    )
    public ResponseMessage findShopList(final ShopListFindRequestParameters parameters) {
        final List<Shop> foundShops = shopAdapterRepository.findAll(parameters.searchType().getEntityName(),
            parameters.page(), parameters.count(), parameters.orderType().name());
        return ResponseMessage.createResponseMessage(createShopListFindResults(foundShops));
    }

    private List<ShopListFindResult> createShopListFindResults(final List<Shop> foundShops) {

        final List<Long> shopIds = foundShops.stream().map(Shop::getId).toList();
        final Map<Long, List<String>> operationNamesByShopId = findOperationNamesByShops(shopIds);
        final Map<Long, List<String>> facilityNamesByShopId = findFacilityNamesByShops(shopIds);

        return foundShops.stream().map(foundShop -> {
                final Long shopId = foundShop.getId();
                final List<String> operationNames = operationNamesByShopId.get(shopId);
                final List<String> facilityNames = facilityNamesByShopId.get(shopId);
                final String thumbnailFileId = foundShop.getImageFileIds().get(0);
                final String thumbnailLink = imageProvider.providePreSignedGetUrlByFileId(thumbnailFileId).preSignedUrl();
//                final boolean likePushed = shopLikeService.isLikePushed(memberEmail, shopId);

                return ShopListFindResult.createShopListFindResult(foundShop, operationNames,
                    facilityNames, thumbnailLink);
            })
            .collect(Collectors.toList());
    }

    private Map<Long, List<String>> findOperationNamesByShops(final List<Long> shopIds) {
        final List<ShopOperation> foundShopOperations = shopOperationService.findShopOperationsByShopIds(
            shopIds);

        final Map<Long, List<Long>> operationIdsByShopId = foundShopOperations.stream()
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

    private Map<Long, List<String>> findFacilityNamesByShops(final List<Long> shopIds) {
        final List<ShopFacility> foundShopFacilities = shopFacilityService.findShopFacilitiesByShopIds(
            shopIds);

        final Map<Long, List<Long>> facilityIdsByShopId = foundShopFacilities.stream()
            .collect(Collectors.groupingBy(shopFacility -> shopFacility.getId().getShopId(),
                Collectors.mapping(shopFacility -> shopFacility.getId().getFacilityId(),
                    Collectors.toList())));

        return facilityIdsByShopId.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                facilityService.findFacilitiesByIds(entry.getValue()).stream()
                    .map(Facility::getName).collect(Collectors.toList())));
    }

    public Shop findShopById(final Long shopId) {
        return shopAdapterRepository.findById(shopId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.SH001));
    }
}
