package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.Category;
import com.beautify_project.bp_app_api.entity.ShopCategory;
import com.beautify_project.bp_app_api.repository.ShopCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopCategoryService {

    private final ShopCategoryRepository shopCategoryRepository;
    private final OperationCategoryService operationCategoryService;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopCategory> registerShopCategories(final String shopId, final List<String> operationIds) {
        final List<Category> categories = operationCategoryService.findCategoriesWithOperationIds(
            operationIds);
        final List<ShopCategory> shopCategories = createShopCategoriesWithShopIdAndCategories(
            shopId, categories);
        return registerAll(shopCategories);
    }

    public List<ShopCategory> createShopCategoriesWithShopIdAndCategories(final String shopId,
        final List<Category> categories) {

        return categories.stream().map(category -> ShopCategory.of(shopId, category.getId()))
            .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopCategory> registerAll(final List<ShopCategory> shopCategories) {
        return shopCategoryRepository.saveAll(shopCategories);
    }

    @Transactional(rollbackFor = Exception.class)
    public ShopCategory registerShopCategory(final ShopCategory shopCategory) {
        return shopCategoryRepository.save(shopCategory);
    }

    @Transactional
    public void remove(final ShopCategory shopCategoryToRemove) {
        shopCategoryRepository.delete(shopCategoryToRemove);
    }
}
