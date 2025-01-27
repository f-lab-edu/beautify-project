package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.ShopOperation;
import com.bp.domain.mysql.repository.ShopOperationRepository;
import com.bp.utils.Validator;
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
    public List<ShopOperation> registerShopOperations(final Long shopId,
        final List<Long> operationIds) {

        final List<Operation> operations = operationService.findOperationsByIds(operationIds);
        final List<ShopOperation> shopOperations = createShopOperationsWithShopIdAndOperations(
            shopId, operations);

        return registerAll(shopOperations);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ShopOperation> registerAll(final List<ShopOperation> shopOperations) {
        return shopOperationRepository.saveAll(shopOperations);
    }

    private List<ShopOperation> createShopOperationsWithShopIdAndOperations(final Long shopId,
        final List<Operation> operations) {
        return operations.stream().map(operation -> ShopOperation.newShopOperation(shopId, operation.getId()))
            .toList();
    }

    public List<ShopOperation> findShopOperationsByShopIds(final List<Long> shopIds) {
        final List<ShopOperation> shopOperations = shopOperationRepository.findByIdShopIdIn(shopIds);
        Validator.throwIfNullOrEmpty(shopOperations, new BpCustomException(ErrorCode.SO001));
        return shopOperations;
    }

    @Transactional
    public void remove(final ShopOperation shopOperationToRemove) {
        shopOperationRepository.delete(shopOperationToRemove);
    }
}
