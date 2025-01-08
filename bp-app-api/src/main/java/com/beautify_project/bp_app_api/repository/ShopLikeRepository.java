package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.entity.ShopLike.ShopLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLikeRepository extends JpaRepository<ShopLike, ShopLikeId>, ShopLikeRepositoryCustom {
}
