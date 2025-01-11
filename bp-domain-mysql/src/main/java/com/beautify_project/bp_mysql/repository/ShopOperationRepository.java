package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopOperation;
import com.beautify_project.bp_mysql.entity.ShopOperation.ShopOperationId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOperationRepository extends JpaRepository<ShopOperation, ShopOperationId> {
    List<ShopOperation> findByIdShopIdIn(final List<String> shopIds);
}
