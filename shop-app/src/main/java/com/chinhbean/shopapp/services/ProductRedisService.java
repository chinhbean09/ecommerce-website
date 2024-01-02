package com.chinhbean.shopapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chinhbean.shopapp.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRedisService implements IProductRedisService{
    //convert, ánh xạ data trong product response sang dạng string, JSON string sau đó lưu vào trong database
    //lấy ra cũng là JSON string và chuyền ngược lại đối tượng product response
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    //Khóa này sẽ được sử dụng để lưu trữ và truy xuất dữ liệu từ Redis.
    private String getKeyFrom(String keyword,
                       Long categoryId,
                       PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id")
                .getDirection() == Sort.Direction.ASC ? "asc": "desc";
        String key = String.format("all_products:%d:%d:%s", pageNumber, pageSize, sortDirection);
        return key;
        /*
        {
            "all_products:1:10:asc": "list of products object"
        }
        * */
    }
    //lấy danh sách sản phẩm từ Redis dựa trên khóa getKeyFrom, sử dụng opsForValue() để lấy giá trị từ Redis,
    //sau đó chuyển đổi chuỗi JSON thành danh sách đối tượng ProductResponse bằng cách sử dụng ObjectMapper.
    @Override
    public List<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId,
                                                PageRequest pageRequest) throws JsonProcessingException {

        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = (String) redisTemplate.opsForValue().get(key);
        List<ProductResponse> productResponses = json != null ?
                redisObjectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {})
                : null;
        return productResponses;
    }

    //xóa toàn bộ dữ liệu trong cơ sở dữ liệu Redis bằng cách sử dụng flushAll() trên kết nối Redis
    @Override
    public void clear(){
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    //sử dụng opsForValue() để đặt giá trị trong Redis. Dữ liệu được chuyển đổi
    //thành chuỗi JSON bằng ObjectMapper trước khi lưu trữ. Khóa được tạo bởi getKeyFrom
    public void saveAllProducts(List<ProductResponse> productResponses,
                                String keyword,
                                Long categoryId,
                                PageRequest pageRequest) throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
// Sử dụng ObjectMapper(redisObjectMapper) để chuyển đối tượng productResponses
// thành một chuỗi JSON (json) để lưu vào redis.
        String json = redisObjectMapper.writeValueAsString(productResponses);
        //opsForValue() để có được ValueOperations và sau đó sử dụng set
        //để lưu trữ giá trị(json) vào Redis với khóa (key) tương ứng.
        redisTemplate.opsForValue().set(key, json);
    }

    //Dưới đây là một số công việc cụ thể mà RedisTemplate thường thực hiện:
    //
    //Lưu trữ và Truy xuất Dữ liệu:
    //
    //opsForValue(): Cung cấp phương thức để thực hiện các thao tác liên quan đến giá trị (value)
    // trong Redis. Điều này bao gồm lưu trữ và truy xuất giá trị theo khóa.
    //opsForList(), opsForSet(), opsForHash(): Tương tự, cung cấp các phương thức để thao tác
    // với danh sách, tập hợp và bảng băm trong Redis.
}
