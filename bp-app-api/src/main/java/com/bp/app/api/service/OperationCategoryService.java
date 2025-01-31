package com.bp.app.api.service;

import com.bp.domain.mysql.entity.Category;
import com.bp.domain.mysql.entity.OperationCategory;
import com.bp.domain.mysql.repository.OperationCategoryAdapterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationCategoryService {

    private final OperationCategoryAdapterRepository operationCategoryAdapterRepository;
    private final CategoryService categoryService;

    public List<Category> findCategoriesWithOperationIds(final List<Long> operationIds) {
        final List<OperationCategory> operationCategories = findOperationCategoriesWithOperationIds(
            operationIds);

        final List<Long> categoryIds = operationCategories.stream()
            .map(operationCategory -> operationCategory.getId().getCategoryId()).toList();

        return categoryService.findCategoriesByIds(categoryIds);
    }

    public List<OperationCategory> findOperationCategoriesWithOperationIds(
        final List<Long> operationIds) {
        return operationCategoryAdapterRepository.findByIdOperationIdIn(operationIds);
    }
}
