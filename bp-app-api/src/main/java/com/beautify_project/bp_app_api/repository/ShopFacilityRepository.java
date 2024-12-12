package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopFacility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopFacilityRepository extends JpaRepository<ShopFacility, String> {
    List<ShopFacility> findByShopIdIn(final List<String> shopIds);

}
