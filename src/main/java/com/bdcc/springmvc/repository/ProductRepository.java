package com.bdcc.springmvc.repository;

import com.bdcc.springmvc.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Recherche par mot-cle (insensible a la casse) avec pagination. */
    Page<Product> findByNameContainsIgnoreCase(String keyword, Pageable pageable);
}
