package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Category;
import com.bp.domain.mysql.entity.OperationCategory;
import com.bp.domain.mysql.repository.OperationCategoryRepository;
import com.bp.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationCategoryService {

    private final OperationCategoryRepository operationCategoryRepository;
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
        final List<OperationCategory> operationCategories = operationCategoryRepository.findByIdOperationIdIn(
            operationIds);
        Validator.throwIfNullOrEmpty(operationCategories, new BpCustomException(ErrorCode.OC001));
        return operationCategoryRepository.findByIdOperationIdIn(operationIds);
    }
}
