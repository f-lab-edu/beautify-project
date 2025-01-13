package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.dto.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_mysql.entity.Category;
import com.beautify_project.bp_mysql.entity.OperationCategory;
import com.beautify_project.bp_mysql.repository.OperationCategoryRepository;
import com.beautify_project.bp_utils.Validator;
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

    public List<Category> findCategoriesWithOperationIds(final List<String> operationIds) {
        final List<OperationCategory> operationCategories = findOperationCategoriesWithOperationIds(
            operationIds);

        final List<String> categoryIds = operationCategories.stream()
            .map(operationCategory -> operationCategory.getId().getCategoryId()).toList();

        return categoryService.findCategoriesByIds(categoryIds);
    }

    public List<OperationCategory> findOperationCategoriesWithOperationIds(
        final List<String> operationIds) {
        final List<OperationCategory> operationCategories = operationCategoryRepository.findByIdOperationIdIn(
            operationIds);
        Validator.throwIfNullOrEmpty(operationCategories, new BpCustomException(ErrorCode.OC001));
        return operationCategoryRepository.findByIdOperationIdIn(operationIds);
    }
}
