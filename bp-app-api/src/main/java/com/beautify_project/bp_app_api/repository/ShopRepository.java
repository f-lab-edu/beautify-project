package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, String> {

    Page<Shop> findAll(Pageable pageable);
}
