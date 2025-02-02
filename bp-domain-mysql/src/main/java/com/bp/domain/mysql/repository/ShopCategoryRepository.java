package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopCategory;
import com.bp.domain.mysql.entity.ShopCategory.ShopCategoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCategoryRepository extends JpaRepository<ShopCategory, ShopCategoryId> {

    List<ShopCategory> findByIdShopId(Long shopId);
}
