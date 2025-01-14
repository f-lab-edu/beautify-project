package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.entity.adapter.ShopAdapter;
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

    public List<ShopAdapter> findByIdIn(final Set<Long> shopIdsToFind) {
        final List<Shop> shopEntities = shopRepository.findByIdIn(shopIdsToFind);
        return shopEntities.stream().map(ShopAdapter::toAdapter).toList();
    }

    @Transactional
    public void saveAll(final Collection<ShopAdapter> foundShops) {
        final List<Shop> entities = foundShops.stream().map(Shop::from).toList();
        shopRepository.saveAll(entities);
    }
}
