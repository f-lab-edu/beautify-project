package com.bp.app.api.service;

import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import com.bp.domain.mysql.repository.ShopLikeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ShopLikeService {

    private static final ShopLike EMPTY_SHOP_LIKE = ShopLike.newShopLike(null, null);

    private final ShopLikeRepository shopLikeRepository;

    public boolean isLikePushed(final Long shopId, final String memberEmail) {
        ShopLike foundshopLike = shopLikeRepository.findById(ShopLikeId.newShopLikeId(shopId, memberEmail))
            .orElseGet(() -> EMPTY_SHOP_LIKE);
        return !foundshopLike.isEmpty();
    }

    @Transactional
    public ShopLike registerShopLike(final ShopLike shopLikeToRegister) {
        return shopLikeRepository.save(shopLikeToRegister);
    }

    @Transactional
    public void saveAllShopLikes(final List<ShopLike> shopLikesToRegister) {
        // TODO: jdbc 사용하는 방식으로 변경?
        try {
            shopLikeRepository.bulkInsert(shopLikesToRegister);
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            saveShopLikeWithoutDuplicatedKey(shopLikesToRegister);
        }
    }

    @Transactional
    private void saveShopLikeWithoutDuplicatedKey(final List<ShopLike> shopLikesToRegister) {
        // TODO: org.springframework.transaction.UnexpectedRollbackException: Transaction silently rolled back because it has been marked as rollback-only 해결 필요
        for (ShopLike shopLike : shopLikesToRegister) {
            try {
                shopLikeRepository.save(shopLike);
            } catch (DuplicateKeyException duplicateKeyException) {
                log.error("Failed to insert ShopLike {}", shopLike, duplicateKeyException);
            }
        }
    }

    @Transactional
    public void deleteShopLike(final ShopLike shopLikeToDelete) {
        shopLikeRepository.delete(shopLikeToDelete);
    }

    @Transactional
    public void deleteAllShopLikes(final List<ShopLike> shopLikesToDelete) {
        shopLikeRepository.deleteAllInBatch(shopLikesToDelete);
    }
}
