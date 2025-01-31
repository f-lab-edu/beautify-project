package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Shop;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Page<Shop> findAll(Pageable pageable);
//    List<Shop> findByIdIn(final List<String> shopIds);

    List<Shop> findByIdIn(final Set<Long> shopIds);
}
