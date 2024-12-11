package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
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
import com.beautify_project.bp_app_api.repository.ShopRepository;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerShop(final ShopRegistrationRequest shopRegistrationRequest) {
        final Shop registeredShop = shopRepository.save(Shop.from(shopRegistrationRequest));
        return ResponseMessage.createResponseMessage(
            new ShopRegistrationResult(registeredShop.getId()));
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

        final List<ShopListFindResult> shopListFindResults = new ArrayList<>();

        for (Shop foundShop : foundShops) {
            final String shopId = foundShop.getId();
            final List<String> operationNames = operationNamesByShopId.get(shopId);
            final List<String> facilityNames = facilityNamesByShopId.get(shopId);

            shopListFindResults.add(
                ShopListFindResult.createShopListFindResult(foundShop, operationNames, facilityNames));
        }

        return shopListFindResults;
    }

    private Map<String, List<String>> findOperationNamesByShops(final List<String> shopIds) {
        final List<ShopOperation> shopOperations = shopOperationService.findShopOperationsByShopIds(
            shopIds);

        final Map<String, List<String>> operationIdsByShopId = new HashMap<>();
        for (ShopOperation shopOperation : shopOperations) {
            addOperationIdsByShopId(shopOperation, operationIdsByShopId);
        }

        final Map<String, List<String>> operationNamesByShopId = new HashMap<>();
        for (Entry<String, List<String>> entry : operationIdsByShopId.entrySet()) {
            final String shopId = entry.getKey();
            final List<String> operationIds = operationIdsByShopId.get(shopId);
            final List<String> operationNames = operationService.findOperationsByIds(operationIds)
                .stream().map(Operation::getName).toList();
            operationNamesByShopId.put(shopId, operationNames);
        }

        return operationNamesByShopId;
    }

    private void addOperationIdsByShopId(final ShopOperation shopOperation,
        final Map<String, List<String>> operationIdsByShopId) {

        final String shopId = shopOperation.getShopId();
        List<String> operationIds;

        if (operationIdsByShopId.containsKey(shopId)) {
            operationIds = operationIdsByShopId.get(shopId);
            operationIds.add(shopOperation.getOperationId());
        } else {
            operationIds = new ArrayList<>();
            operationIds.add(shopOperation.getOperationId());
            operationIdsByShopId.put(shopId, operationIds);
        }
    }

    private Map<String, List<String>> findFacilityNamesByShops(final List<String> shopIds) {
        final List<ShopFacility> shopFacilities = shopFacilityService.findShopFacilitiesByShopIds(
            shopIds);

        final Map<String, List<String>> facilityIdsByShopId = new HashMap<>();
        for (ShopFacility shopFacility : shopFacilities) {
            addFacilityIdsByShopId(shopFacility, facilityIdsByShopId);
        }

        final Map<String, List<String>> facilityNamesByShopId = new HashMap<>();
        for (Entry<String, List<String>> entry : facilityIdsByShopId.entrySet()) {
            final String shopId = entry.getKey();
            final List<String> facilityIds = facilityIdsByShopId.get(shopId);
            final List<String> facilityNames = facilityService.findFacilitiesByIds(facilityIds)
                .stream().map(Facility::getName).toList();
            facilityNamesByShopId.put(shopId, facilityNames);
        }

        return facilityNamesByShopId;
    }

    private void addFacilityIdsByShopId(final ShopFacility shopFacility,
        final Map<String, List<String>> facilityIdsByShopId) {

        final String shopId = shopFacility.getShopId();
        List<String> facilityIds;

        if (facilityIdsByShopId.containsKey(shopId)) {
            facilityIds = facilityIdsByShopId.get(shopId);
            facilityIds.add(shopFacility.getFacilityId());
        } else {
            facilityIds = new ArrayList<>();
            facilityIds.add(shopFacility.getFacilityId());
            facilityIdsByShopId.put(shopId, facilityIds);
        }
    }

    public Shop findShopById(final @NotBlank String shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SH001));
    }

    @Transactional
    public void likeShop(final @NotBlank String shopId, final @NotBlank String memberEmail) {
        if (shopLikeService.isLikePushed(shopId, memberEmail)) {
            throw new AlreadyProcessedException(ErrorCode.AL001);
        }

        Shop foundShop = findShopById(shopId);
        log.debug("shop like count before add like: shopId - {}, likeCount - {}",
            foundShop.getId(), foundShop.getLikes());
        foundShop.increaseLikeCount();
        shopRepository.save(foundShop);
        // TODO: bearer token 에서 사용자 정보 추출하는 로직 필요
        shopLikeService.registerShopLike(ShopLike.of(shopId, memberEmail));
    }

    @Transactional
    public void cancelLikeShop(final @NotBlank String shopId, final @NotBlank String memberEmail) {
        if (!shopLikeService.isLikePushed(shopId, memberEmail)) {
            throw new AlreadyProcessedException(ErrorCode.AL002);
        }

        Shop foundShop = findShopById(shopId);
        log.debug("shop like count before subtract like: shopId - {}, likeCount - {}",
            foundShop.getId(), foundShop.getLikes());
        foundShop.decreaseLikeCount();
        shopRepository.save(foundShop);
        // TODO: bearer token 에서 사용자 정보 추출하는 로직 필요
        shopLikeService.deleteShopLike(ShopLike.of(shopId, memberEmail));
    }
}
