package com.chinhbean.shopapp.services.product;

import com.chinhbean.shopapp.dtos.ProductDTO;
import com.chinhbean.shopapp.dtos.ProductImageDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.exceptions.InvalidParamException;
import com.chinhbean.shopapp.models.*;
import com.chinhbean.shopapp.repositories.CategoryRepository;
import com.chinhbean.shopapp.repositories.ProductImageRepository;
import com.chinhbean.shopapp.repositories.ProductRepository;
import com.chinhbean.shopapp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId, PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
    ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception;

    List<Product> findProductsByIds(List<Long> productIds);
    String storeFile(MultipartFile file) throws IOException;
    void deleteFile(String filename) throws IOException;

    @Service
    @RequiredArgsConstructor
    class ProductService implements IProductRedisService.IProductService {
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;
        private final ProductImageRepository productImageRepository;
        @Override
        @Transactional
        public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() ->  new DataNotFoundException(
                                    "Cannot find category with id: "+ productDTO.getCategoryId()));
    //ánh xạ DTO sang entity
            Product newProduct = Product.builder()
                    .name(productDTO.getName())
                    .price(productDTO.getPrice())
                    .thumbnail(productDTO.getThumbnail())
                    .description(productDTO.getDescription())
                    .category(existingCategory)
                    .build();
            return productRepository.save(newProduct);
        }

        @Override
        public Product getProductById(long productId) throws Exception {
            Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
            if(optionalProduct.isPresent()){
                return optionalProduct.get();
            }
            throw new DataNotFoundException(
                            "Cannot find product with id ="+productId);
        }
        @Override
        public List<Product> findProductByIds(List<Long> productIds){
            return productRepository.findProductsByIds(productIds);
        }


        public Page<ProductResponse> getAllProducts(String keyword,Long categoryId, PageRequest pageRequest) {
            // Lấy danh sách sản phẩm theo trang(page) và giới hạn(limit)
            Page<Product> productsPage;
            productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
            return productsPage.map(ProductResponse::fromProduct);
        }

        @Override
        @Transactional
        public Product updateProduct(long id, ProductDTO productDTO) throws Exception {

            Product existingProduct = getProductById(id);
            if (existingProduct != null) {
                //copy các thuộc tính từ DTO -> Product
                //Có thể sử dụng ModelMapper
                Category existingCategory = categoryRepository
                        .findById(productDTO.getCategoryId())
                        .orElseThrow(() ->
                                new DataNotFoundException(
                                        "Cannot find category with id: " + productDTO.getCategoryId()));
                if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
                    existingProduct.setName(productDTO.getName());
                }

                existingProduct.setCategory(existingCategory);
                if (productDTO.getPrice() >= 0) {
                    existingProduct.setPrice(productDTO.getPrice());
                }
                if (productDTO.getDescription() != null &&
                        !productDTO.getDescription().isEmpty()) {
                    existingProduct.setDescription(productDTO.getDescription());
                }
                if (productDTO.getThumbnail() != null &&
                        !productDTO.getThumbnail().isEmpty()) {
                    existingProduct.setDescription(productDTO.getThumbnail());
                }
                return productRepository.save(existingProduct);
            }
            return null;
        }

        @Override
        @Transactional
        public void deleteProduct(long id) {
            //optional đại diện cho có hoặc không có giá trị.
            Optional<Product> optionalProduct = productRepository.findById(id);
            optionalProduct.ifPresent(productRepository::delete);
        }

        @Override
        public boolean existsByName(String name) {
            return productRepository.existsByName(name);
        }
        @Override
        @Transactional
        public ProductImage createProductImage( Long productId, ProductImageDTO productImageDTO) throws Exception {

            Product existingProduct = productRepository
                    .findById(productId)
                    .orElseThrow(() ->
                            new DataNotFoundException(
                                    "Cannot find product with id: "+productImageDTO.getProductId()));
            ProductImage newProductImage = ProductImage.builder()
                    .product(existingProduct)
                    .imageUrl(productImageDTO.getImageUrl())
                    .build();
            //Ko cho insert quá 5 ảnh cho 1 sản phẩm
            int size = productImageRepository.findByProductId(productId).size();
            if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                throw new InvalidParamException(
                        "Number of images must be <= "
                                +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
            }
            return productImageRepository.save(newProductImage);
        }
    }
}