package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.ShopFacility;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopFacilityAdapterRepository {

    private final ShopFacilityRepository defaultRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopFacility> saveAll(final List<ShopFacility> shopFacilitiesToSave) {
        return defaultRepository.saveAll(shopFacilitiesToSave);
    }

    @ReadOnlyTransactional
    public List<ShopFacility> findByIdShopIdIn(final List<Long> shopIdsToFind) {
        return defaultRepository.findByIdShopIdIn(shopIdsToFind);
    }

    public void delete(final ShopFacility shopFacilityToDelete) {
        defaultRepository.delete(shopFacilityToDelete);
    }

}
