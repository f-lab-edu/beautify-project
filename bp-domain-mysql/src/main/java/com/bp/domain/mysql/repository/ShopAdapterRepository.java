package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Shop;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopAdapterRepository {

    private final ShopRepository shopRepository;

    public List<Shop> findByIdIn(final Set<Long> shopIdsToFind) {
        return shopRepository.findByIdIn(shopIdsToFind);
    }

    @Transactional
    public void saveAll(final Collection<Shop> shopsToSave) {
        shopRepository.saveAll(shopsToSave);
    }
}
