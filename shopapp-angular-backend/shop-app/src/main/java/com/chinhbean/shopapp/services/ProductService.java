    package com.chinhbean.shopapp.services;

    import com.chinhbean.shopapp.dtos.ProductDTO;
    import com.chinhbean.shopapp.dtos.ProductImageDTO;
    import com.chinhbean.shopapp.exceptions.DataNotFoundException;
    import com.chinhbean.shopapp.exceptions.InvalidParamException;
    import com.chinhbean.shopapp.models.Category;
    import com.chinhbean.shopapp.models.Product;
    import com.chinhbean.shopapp.models.ProductImage;
    import com.chinhbean.shopapp.repositories.CategoryRepository;
    import com.chinhbean.shopapp.repositories.ProductImageRepository;
    import com.chinhbean.shopapp.repositories.ProductRepository;
    import com.chinhbean.shopapp.responses.ProductResponse;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    @RequiredArgsConstructor
    public class ProductService implements IProductService{
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
            if(existingProduct != null) {
                //copy các thuộc tính từ DTO -> Product
                //Có thể sử dụng ModelMapper
                Category existingCategory = categoryRepository
                        .findById(productDTO.getCategoryId())
                        .orElseThrow(() -> new DataNotFoundException(
                                "Cannot find category with id: "+ productDTO.getCategoryId()));
                existingProduct.setName(productDTO.getName());
                existingProduct.setCategory(existingCategory);
                existingProduct.setPrice(productDTO.getPrice());
                existingProduct.setDescription(productDTO.getDescription());
                existingProduct.setThumbnail(productDTO.getThumbnail());
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
