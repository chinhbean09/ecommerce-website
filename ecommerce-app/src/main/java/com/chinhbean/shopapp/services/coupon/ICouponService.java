package com.chinhbean.shopapp.services.coupon;

public interface ICouponService {
    double calculateCouponValue(String couponCode, double totalAmount);
}
