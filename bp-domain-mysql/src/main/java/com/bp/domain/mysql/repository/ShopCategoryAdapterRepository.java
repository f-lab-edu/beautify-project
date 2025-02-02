package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopCategoryAdapterRepository {

    private final ShopCategoryRepository defaultRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopCategory> saveAll(final List<ShopCategory> shopCategoriesToSave) {
        return defaultRepository.saveAll(shopCategoriesToSave);
    }

    public ShopCategory save(final ShopCategory shopCategoryToSave) {
        return defaultRepository.save(shopCategoryToSave);
    }

    public void delete(final ShopCategory shopCategoryToDelete) {
        defaultRepository.delete(shopCategoryToDelete);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }

    public List<ShopCategory> findByIdShopId(final Long shopId) {
        return defaultRepository.findByIdShopId(shopId);
    }
}
