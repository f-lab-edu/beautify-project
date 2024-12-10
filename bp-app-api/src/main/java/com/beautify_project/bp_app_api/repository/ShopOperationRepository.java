package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopOperation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOperationRepository extends JpaRepository<ShopOperation, String> {
    List<ShopOperation> findByShopIdIn(final List<String> shopIds);
}
