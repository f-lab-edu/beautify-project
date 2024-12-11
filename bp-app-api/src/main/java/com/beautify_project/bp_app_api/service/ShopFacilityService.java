package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.ShopFacility;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ShopFacilityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopFacilityService {

    private final ShopFacilityRepository shopFacilityRepository;

    public List<ShopFacility> findShopFacilitiesByShopIds(final List<String> shopIds) {
        List<ShopFacility> shopFacilities = shopFacilityRepository.findByShopIdIn(shopIds);
        if (shopFacilities == null || shopFacilities.isEmpty()) {
            throw new NotFoundException(ErrorCode.SF001);
        }
        return shopFacilities;
    }
}
