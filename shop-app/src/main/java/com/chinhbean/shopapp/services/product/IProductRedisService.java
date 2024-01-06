package com.chinhbean.shopapp.services.product;

import com.chinhbean.shopapp.dtos.ProductDTO;
import com.chinhbean.shopapp.dtos.ProductImageDTO;
import com.chinhbean.shopapp.models.Product;
import com.chinhbean.shopapp.models.ProductImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.chinhbean.shopapp.responses.ProductResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IProductRedisService {
    //Clear cached data in Redis
    void clear();//clear cache
    List<ProductResponse> getAllProducts(
            String keyword,
            Long categoryId, PageRequest pageRequest) throws JsonProcessingException;
    void saveAllProducts(List<ProductResponse> productResponses,
                                String keyword,
                                Long categoryId,
                                PageRequest pageRequest) throws JsonProcessingException;


}
