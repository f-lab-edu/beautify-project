package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_mysql.entity.Operation;
import com.beautify_project.bp_mysql.entity.ShopOperation;
import com.beautify_project.bp_mysql.repository.ShopOperationRepository;
import com.beautify_project.bp_utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopOperationService {

    private final ShopOperationRepository shopOperationRepository;
    private final OperationService operationService;

    @Transactional(rollbackFor = Exception.class)
    public List<ShopOperation> registerShopOperations(final String shopId,
        final List<String> operationIds) {

        final List<Operation> operations = operationService.findOperationsByIds(operationIds);
        final List<ShopOperation> shopOperations = createShopOperationsWithShopIdAndOperations(
            shopId, operations);

        return registerAll(shopOperations);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopOperation> registerAll(final List<ShopOperation> shopOperations) {
        return shopOperationRepository.saveAll(shopOperations);
    }

    private List<ShopOperation> createShopOperationsWithShopIdAndOperations(final String shopId,
        final List<Operation> operations) {
        return operations.stream().map(operation -> ShopOperation.of(shopId, operation.getId()))
            .toList();
    }

    public List<ShopOperation> findShopOperationsByShopIds(final List<String> shopIds) {
        final List<ShopOperation> shopOperations = shopOperationRepository.findByIdShopIdIn(shopIds);
        Validator.throwIfNullOrEmpty(shopOperations, new BpCustomException(ErrorCode.SO001));
        return shopOperations;
    }

    @Transactional
    public void remove(final ShopOperation shopOperationToRemove) {
        shopOperationRepository.delete(shopOperationToRemove);
    }
}
