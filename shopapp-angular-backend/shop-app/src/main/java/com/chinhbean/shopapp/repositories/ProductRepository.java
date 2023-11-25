package com.chinhbean.shopapp.repositories;

import com.chinhbean.shopapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Page<Product> findAllBy(Pageable pageable);//phân trang


}
