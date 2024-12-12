package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLikeRepository extends JpaRepository<ShopLike, String> {

    Long countByShopId(final String shopId);
}
