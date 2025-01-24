package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Facility;
import com.bp.domain.mysql.entity.ShopFacility;
import com.bp.domain.mysql.repository.ShopFacilityRepository;
import com.bp.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopFacilityService {

    private final ShopFacilityRepository shopFacilityRepository;
    private final FacilityService facilityService;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopFacility> registerShopFacilities(final Long shopId,
        final List<String> facilityIds) {

        final List<Facility> facilities = facilityService.findFacilitiesByIds(facilityIds);
        final List<ShopFacility> shopFacilitiesToRegister = createShopFacilitiesWithShopIdAndFacilities(
            shopId, facilities);

        return registerAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> createShopFacilitiesWithShopIdAndFacilities(final Long shopId,
        final List<Facility> facilities) {
        return facilities.stream().map(facility -> ShopFacility.of(shopId, facility.getId()))
            .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopFacility> registerAll(final List<ShopFacility> shopFacilitiesToRegister) {
        return shopFacilityRepository.saveAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> findShopFacilitiesByShopIds(final List<Long> shopIds) {
        Validator.throwIfNullOrEmpty(shopIds, new BpCustomException(ErrorCode.BR001));
        return shopFacilityRepository.findByIdShopIdIn(shopIds);
    }

    @Transactional
    public void remove(final ShopFacility shopFacilityToRemove) {
        shopFacilityRepository.delete(shopFacilityToRemove);
    }
}
