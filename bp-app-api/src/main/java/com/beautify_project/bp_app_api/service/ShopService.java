package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.IdName;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.repository.CategoryRepository;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import com.beautify_project.bp_app_api.repository.OperationRepository;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
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
    private final StorageService storageService;
    private final FacilityRepository facilityRepository;
    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerShop(final ImageFiles images,
        final ShopRegistrationRequest shopRegistrationRequest) {

        // TODO: ShopRegistrationRequest 내 IdName 제거하고 id 값만 string 으로 받을 수 있게 수정
        // TODO: ShopRegistrationRequest 내 category 객체 삭제
        // TODO: ShopRegistrationRequest 변동 내용 문서 반영
        long registerTime = System.currentTimeMillis();
        final List<String> operationIds = shopRegistrationRequest.operations().stream().map(IdName::id)
            .toList();
        final List<String> facilityIds = shopRegistrationRequest.supportFacilities().stream()
            .map(IdName::id).toList();

        List<Operation> operations = operationRepository.findByIdIn(operationIds);
        List<Facility> facilities = facilityRepository.findByIdIn(facilityIds);

        final Shop regisertedShop = shopRepository.save(
            Shop.createShop(shopRegistrationRequest, operations, facilities, registerTime));

        if (!images.isEmpty()) {
            storageService.storeImageFiles(images, regisertedShop.getId());
        }

        return ResponseMessage.createResponseMessage(
            new ShopRegistrationResult(regisertedShop.getId()));
    }

    public ResponseMessage findShopList(final ShopFindListRequestParameters parameters) {
        Pageable pageable = PageRequest.of(parameters.page(), parameters.count(),
            Sort.by(Sort.Direction.fromString(parameters.orderType().name()),
                parameters.searchType().getEntityName()));

        Page<ShopListFindResult> page = shopRepository.findAll(pageable)
            .map(foundShop ->
                ShopListFindResult.createShopListFindResult(foundShop,
                    new String(Base64.getEncoder().encode(storageService.loadThumbnail(
                        foundShop.getId())))));

        return ResponseMessage.createResponseMessage(page.getContent());
    }
}
