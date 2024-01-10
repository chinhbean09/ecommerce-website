package com.chinhbean.shopapp.repositories;

import com.chinhbean.shopapp.models.Category;
import com.chinhbean.shopapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);//phân trang

    List<Product> findByCategory(Category category);

    //tức là nếu category không truyền vào thì nó sẽ lấy tất cả
    //nếu keyword truyền vào mà trống thì cũng lấy tất cả
    //keyword = name, category.id = :categoryId

    @Query("SELECT p FROM Product p WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword, Pageable pageable);
//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
//    Optional<Product> getDetailProduct(@Param("productId") Long productId);
@Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImages WHERE p.id = :productId")
Optional<Product> getDetailProduct(@Param("productId") Long productId);

//phục vụ cho cart để lấy nhiều ids sản phẩm
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);

}
