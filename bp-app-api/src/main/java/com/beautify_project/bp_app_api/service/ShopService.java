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
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final FacilityService facilityService;
    private final OperationService operationService;
    private final ImageService imageService;

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerShop(final ShopRegistrationRequest shopRegistrationRequest) {

        long registerTime = System.currentTimeMillis();

        final List<Operation> operations = operationService.findOperationsByIds(
            shopRegistrationRequest.operationIds());
        final List<Facility> facilities = facilityService.findFacilitiesByIds(
            shopRegistrationRequest.facilityIds());

        final Shop regisertedShop = shopRepository.save(
            Shop.createShop(shopRegistrationRequest, operations, facilities, registerTime));

        return ResponseMessage.createResponseMessage(
            new ShopRegistrationResult(regisertedShop.getId()));
    }

    public ResponseMessage findShopList(final ShopListFindRequestParameters parameters) {
        Pageable pageable = PageRequest.of(parameters.page(), parameters.count(),
            Sort.by(Sort.Direction.fromString(parameters.orderType().name()),
                parameters.searchType().getEntityName()));

        final List<Shop> foundShops = shopRepository.findAll(pageable).getContent();
        final List<String> thumbnailFileIds = foundShops.stream()
            .map(foundShop -> foundShop.getImageFileIds().get(0)).toList();

        final List<String> thumbnailLinks = imageService.issuePreSignedGetUrls(thumbnailFileIds);

        final List<ShopListFindResult> shopListFindResults = new ArrayList<>();
        for (int i = 0; i < foundShops.size(); i++) {
            shopListFindResults.add(
                ShopListFindResult.of(foundShops.get(i), thumbnailLinks.get(i)));
        }

        return ResponseMessage.createResponseMessage(shopListFindResults);
    }

    public Shop findShopById(final @NotNull String shopId) {
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.SH001));
    }
}
