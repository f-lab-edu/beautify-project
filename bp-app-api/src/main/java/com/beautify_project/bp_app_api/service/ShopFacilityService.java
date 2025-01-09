package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.ShopFacility;
import com.beautify_project.bp_app_api.enumeration.EntityType;
import com.beautify_project.bp_app_api.exception.InvalidIdException;
import com.beautify_project.bp_app_api.repository.ShopFacilityRepository;
import com.beautify_project.bp_app_api.utils.Validator;
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
    public List<ShopFacility> registerShopFacilities(final String shopId,
        final List<String> facilityIds) {

        final List<Facility> facilities = facilityService.findFacilitiesByIds(facilityIds);
        final List<ShopFacility> shopFacilitiesToRegister = createShopFacilitiesWithShopIdAndFacilities(
            shopId, facilities);

        return registerAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> createShopFacilitiesWithShopIdAndFacilities(final String shopId,
        final List<Facility> facilities) {
        return facilities.stream().map(facility -> ShopFacility.of(shopId, facility.getId()))
            .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopFacility> registerAll(final List<ShopFacility> shopFacilitiesToRegister) {
        return shopFacilityRepository.saveAll(shopFacilitiesToRegister);
    }

    public List<ShopFacility> findShopFacilitiesByShopIds(final List<String> shopIds) {
        Validator.throwIfNullOrEmpty(shopIds,
            new InvalidIdException(EntityType.SHOP, "shopId", "null"));
        return shopFacilityRepository.findByIdShopIdIn(shopIds);
    }

    @Transactional
    public void remove(final ShopFacility shopFacilityToRemove) {
        shopFacilityRepository.delete(shopFacilityToRemove);
    }
}
