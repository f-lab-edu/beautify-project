package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.repository.ShopLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopLikeService {

    private final ShopLikeRepository shopLikeRepository;

    public Long getTotalCountByShopId(final String shopId) {
        return shopLikeRepository.countByShopId(shopId);
    }

    @Transactional
    public ShopLike registerShopLike(final ShopLike shopLike) {
        return shopLikeRepository.save(shopLike);
    }

}
