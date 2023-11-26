package com.chinhbean.shopapp.services;
import com.chinhbean.shopapp.dtos.ProductDTO;
import com.chinhbean.shopapp.dtos.ProductImageDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.chinhbean.shopapp.models.*;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<Product> getAllProducts(PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(
            Long productId, ProductImageDTO productImageDTO) throws Exception;

}
