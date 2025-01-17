package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.Shop;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> {

    Page<Shop> findAll(Pageable pageable);
//    List<Shop> findByIdIn(final List<String> shopIds);

    List<Shop> findByIdIn(final Set<Long> shopIds);
}
