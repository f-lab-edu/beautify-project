package com.bp.app.api.service;

import com.bp.domain.mysql.entity.Category;
import com.bp.domain.mysql.entity.ShopCategory;
import com.bp.domain.mysql.repository.ShopCategoryAdapterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopCategoryService {

    private final ShopCategoryAdapterRepository shopCategoryAdapterRepository;
    private final OperationCategoryService operationCategoryService;

    public List<ShopCategory> registerShopCategories(final Long shopId, final List<Long> operationIds) {
        final List<Category> categories = operationCategoryService.findCategoriesWithOperationIds(
            operationIds);
        final List<ShopCategory> shopCategories = createShopCategoriesWithShopIdAndCategories(
            shopId, categories);
        return registerAll(shopCategories);
    }

    public List<ShopCategory> createShopCategoriesWithShopIdAndCategories(final Long shopId,
        final List<Category> categories) {

        return categories.stream()
            .map(category -> ShopCategory.newShopCategory(shopId, category.getId()))
            .toList();
    }

    public List<ShopCategory> registerAll(final List<ShopCategory> shopCategories) {
        return shopCategoryAdapterRepository.saveAll(shopCategories);
    }

    public ShopCategory registerShopCategory(final ShopCategory shopCategoryToSave) {
        return shopCategoryAdapterRepository.save(shopCategoryToSave);
    }

    public void delete(final ShopCategory shopCategoryToDelete) {
        shopCategoryAdapterRepository.delete(shopCategoryToDelete);
    }
}
