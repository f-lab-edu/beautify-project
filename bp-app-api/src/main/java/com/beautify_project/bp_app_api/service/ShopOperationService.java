package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.ShopOperation;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ShopOperationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopOperationService {

    private final ShopOperationRepository shopOperationRepository;

    public List<ShopOperation> findShopOperationsByShopIds(final List<String> shopIds) {
        final List<ShopOperation> shopOperations = shopOperationRepository.findByShopIdIn(shopIds);
        if (shopOperations == null || shopOperations.isEmpty()) {
            throw new NotFoundException(ErrorCode.SO001);
        }
        return shopOperations;
    }
}
