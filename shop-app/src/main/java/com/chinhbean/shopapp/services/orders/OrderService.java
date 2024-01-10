package com.chinhbean.shopapp.services.orders;

import com.chinhbean.shopapp.dtos.CartItemDTO;
import com.chinhbean.shopapp.dtos.OrderDTO;
import com.chinhbean.shopapp.dtos.OrderDetailDTO;
import com.chinhbean.shopapp.dtos.OrderWithDetailsDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.models.*;
import com.chinhbean.shopapp.repositories.OrderDetailRepository;
import com.chinhbean.shopapp.repositories.OrderRepository;
import com.chinhbean.shopapp.repositories.ProductRepository;
import com.chinhbean.shopapp.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        //tìm xem user'id có tồn tại ko, vay lay id ntn ?
        User user = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: "+ orderDTO.getUserId()));

        //convert orderDTO => Order ánh xạ thuộc tính từ đối tượng OrderDTO sang Order.
        //dùng thư viện Model Mapper
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        // setId được bỏ qua để tránh ghi đè trên id của Order đã tồn tại.
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date());//lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        //Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);

        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());

        //Lưu Order vào cơ sở dữ liệu thông qua orderRepository:
        orderRepository.save(order);

        //===================================================================
        //OrderDetails bắt đầu từ đây, chỉ cần
        //Tạo danh sách các đối tượng OrderDetails để chứa các đối tượng OrderDetail.
        List<OrderDetail> orderDetails  = new ArrayList<>();

        //Duyệt qua từng CartItemDTO trong danh sách cartItems của orderDTO.
        //Đối với mỗi CartItemDTO, một OrderDetail mới được tạo và liên kết với Order hiện tại.
        for(CartItemDTO cartItemDTO: orderDTO.getCartItems()){ //CartItems: 1,2,3, lặp 1,2,3 lần để chi tiết product trong CartItems
            //tạo orderDetail
            OrderDetail orderDetail = new OrderDetail();
            //order detail bắt nnguồn từ Order hiện tại này, 1 order nhiều order detail
            orderDetail.setOrder(order);

            //Lấy thông tin sản phẩm từ cartItemDTO  và sử dụng để đặt thông tin cho OrderDetail.
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ database
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with id " + productId));

            //Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());

            //OrderDetail được thêm vào danh sách orderDetails.
            orderDetails.add(orderDetail);
        }
        //orderDetails được lưu vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        //có một dòng mã có nhiệm vụ tìm kiếm một đối tượng Order trong orderRepository
        Order selectedOrder = orderRepository.findById(id).orElse(null);
        return selectedOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO)
            throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find order with id: " + id));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        //no hard-delete, => please soft-delete
        if(order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }
    @Transactional
    public Order updateOrderWithDetails(OrderWithDetailsDTO orderWithDetailsDTO) {
        modelMapper.typeMap(OrderWithDetailsDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderWithDetailsDTO, order);
        Order savedOrder = orderRepository.save(order);

        // Set the order for each order detail
        for (OrderDetailDTO orderDetailDTO : orderWithDetailsDTO.getOrderDetailDTOS()) {
            //orderDetail.setOrder(OrderDetail);
        }

        // Save or update the order details
        List<OrderDetail> savedOrderDetails = orderDetailRepository.saveAll(order.getOrderDetails());

        // Set the updated order details for the order
        savedOrder.setOrderDetails(savedOrderDetails);

        return savedOrder;
    }
    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }


    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
