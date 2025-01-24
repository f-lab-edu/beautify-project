package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopOperation;
import com.bp.domain.mysql.entity.ShopOperation.ShopOperationId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopOperationRepository extends JpaRepository<ShopOperation, ShopOperationId> {
    List<ShopOperation> findByIdShopIdIn(final List<Long> shopIds);
}
