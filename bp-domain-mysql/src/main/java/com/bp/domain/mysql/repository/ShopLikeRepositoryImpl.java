package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopLike;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShopLikeRepositoryImpl implements ShopLikeRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public void bulkInsert(final List<ShopLike> shopLikes) {
        int batchSize = 50;

        for (int i = 0; i < shopLikes.size(); i++) {
            entityManager.persist(shopLikes.get(i));

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();
        entityManager.close();
    }
}
