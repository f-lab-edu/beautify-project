package com.bp.app.api.service;

import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import com.bp.domain.mysql.repository.ShopLikeAdapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopLikeService {

    private static final ShopLike EMPTY_SHOP_LIKE = ShopLike.newShopLike(null, null);

    private final ShopLikeAdapterRepository shopLikeAdapterRepository;

    public boolean isLikePushed(final String memberEmail, final Long shopId) {
        final ShopLikeId shopLikeIdToFind = ShopLikeId.newShopLikeId(shopId, memberEmail);
        final ShopLike foundShopLike = shopLikeAdapterRepository.findByShopLikeId(shopLikeIdToFind)
            .orElseGet(() -> EMPTY_SHOP_LIKE);
        return !foundShopLike.isEmpty();
    }

}
