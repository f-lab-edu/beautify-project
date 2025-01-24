package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLikeRepository extends JpaRepository<ShopLike, ShopLikeId>, ShopLikeRepositoryCustom {

    List<ShopLike> findByIdIn(List<ShopLikeId> ids);
}
