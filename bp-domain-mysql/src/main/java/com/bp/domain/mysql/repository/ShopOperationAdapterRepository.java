package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.ShopOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopOperationAdapterRepository {

    private final ShopOperationRepository defaultRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopOperation> saveAll(final List<ShopOperation> shopOperationsToSave) {
        return defaultRepository.saveAll(shopOperationsToSave);
    }

    @ReadOnlyTransactional
    public List<ShopOperation> findByIdShopIdIn(final List<Long> shopIdsToFind) {
        return defaultRepository.findByIdShopIdIn(shopIdsToFind);
    }

    public void delete(final ShopOperation shopOperationToDelete) {
        defaultRepository.delete(shopOperationToDelete);
    }

    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }
}
