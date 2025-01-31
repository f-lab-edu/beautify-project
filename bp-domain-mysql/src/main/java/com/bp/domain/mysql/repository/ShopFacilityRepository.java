package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopFacility;
import com.bp.domain.mysql.entity.ShopFacility.ShopFacilityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopFacilityRepository extends JpaRepository<ShopFacility, ShopFacilityId> {
    List<ShopFacility> findByIdShopIdIn(final List<Long> shopIds);

}
