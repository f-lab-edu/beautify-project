package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopCategory;
import com.beautify_project.bp_app_api.entity.ShopCategory.ShopCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCategoryRepository extends JpaRepository<ShopCategory, ShopCategoryId> {

}
