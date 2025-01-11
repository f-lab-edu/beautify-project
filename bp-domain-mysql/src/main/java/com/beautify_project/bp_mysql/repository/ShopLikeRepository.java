package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopLike;
import com.beautify_project.bp_mysql.entity.ShopLike.ShopLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLikeRepository extends JpaRepository<ShopLike, ShopLikeId> {
}
