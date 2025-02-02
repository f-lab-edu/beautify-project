package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Facility;
import com.bp.domain.mysql.entity.ShopFacility;
import com.bp.domain.mysql.repository.ShopFacilityAdapterRepository;
import com.bp.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopFacilityService {

    private final ShopFacilityAdapterRepository shopFacilityAdapterRepository;
    private final FacilityService facilityService;

    public List<ShopFacility> registerShopFacilities(final Long shopId,
        final List<Long> facilityIds) {

        final List<Facility> facilities = facilityService.findFacilitiesByIds(facilityIds);
        final List<ShopFacility> shopFacilitiesToRegister = createShopFacilitiesWithShopIdAndFacilities(
            shopId, facilities);

        return registerAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> createShopFacilitiesWithShopIdAndFacilities(final Long shopId,
        final List<Facility> facilities) {
        return facilities.stream()
            .map(facility -> ShopFacility.newShopFacility(shopId, facility.getId()))
            .toList();
    }

    public List<ShopFacility> registerAll(final List<ShopFacility> shopFacilitiesToRegister) {
        return shopFacilityAdapterRepository.saveAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> findShopFacilitiesByShopIds(final List<Long> shopIds) {
        Validator.throwIfNullOrEmpty(shopIds, new BpCustomException(ErrorCode.BR001));
        return shopFacilityAdapterRepository.findByIdShopIdIn(shopIds);
    }

    public void delete(final ShopFacility shopFacilityToDelete) {
        shopFacilityAdapterRepository.delete(shopFacilityToDelete);
    }
}
