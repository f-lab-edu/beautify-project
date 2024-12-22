package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopFacility;
import com.beautify_project.bp_app_api.entity.ShopFacility.ShopFacilityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopFacilityRepository extends JpaRepository<ShopFacility, ShopFacilityId> {
    List<ShopFacility> findByIdShopIdIn(final List<String> shopIds);

}
