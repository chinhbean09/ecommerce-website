package com.chinhbean.shopapp.services.orderdetails;

import com.chinhbean.shopapp.dtos.OrderDetailDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData)
            throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);


}
