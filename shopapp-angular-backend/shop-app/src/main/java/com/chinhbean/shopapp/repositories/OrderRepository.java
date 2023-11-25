package com.chinhbean.shopapp.repositories;

import com.chinhbean.shopapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //Tìm danh sach đơn hàng của 1 user nào đó
//    List<Order> findByUserId(Long userId);

    List<Order> findByUserId(Long userId);
}
