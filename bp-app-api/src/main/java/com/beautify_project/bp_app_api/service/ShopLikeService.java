package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.entity.ShopLike.ShopLikeId;
import com.beautify_project.bp_app_api.repository.ShopLikeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopLikeService {

    private static final ShopLike EMPTY_SHOP_LIKE = ShopLike.of(null, null);

    private final ShopLikeRepository shopLikeRepository;

    public boolean isLikePushed(final String shopId, final String memberEmail) {
        ShopLike foundshopLike = shopLikeRepository.findById(ShopLikeId.of(shopId, memberEmail))
            .orElseGet(() -> EMPTY_SHOP_LIKE);
        return !foundshopLike.isEmpty();
    }

    @Transactional
    public ShopLike registerShopLike(final ShopLike shopLikeToRegister) {
        return shopLikeRepository.save(shopLikeToRegister);
    }

    @Transactional
    public void registerAllShopLikes(final List<ShopLike> shopLikesToRegister) {
        shopLikeRepository.saveAll(shopLikesToRegister);
    }

    @Transactional
    public void deleteShopLike(final ShopLike shopLikeToDelete) {
        shopLikeRepository.delete(shopLikeToDelete);
    }
}
