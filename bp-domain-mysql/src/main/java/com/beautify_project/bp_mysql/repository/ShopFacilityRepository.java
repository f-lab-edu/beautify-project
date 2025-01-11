package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopFacility;
import com.beautify_project.bp_mysql.entity.ShopFacility.ShopFacilityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopFacilityRepository extends JpaRepository<ShopFacility, ShopFacilityId> {
    List<ShopFacility> findByIdShopIdIn(final List<String> shopIds);

}
