package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopCategory;
import com.beautify_project.bp_mysql.entity.ShopCategory.ShopCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopCategoryRepository extends JpaRepository<ShopCategory, ShopCategoryId> {

}
