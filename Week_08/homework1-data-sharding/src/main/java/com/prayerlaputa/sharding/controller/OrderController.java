package com.prayerlaputa.sharding.controller;

import com.prayerlaputa.sharding.model.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/12/7
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    public boolean insertOrder(Long memberId, BigDecimal payment) {
        return true;
    }

    public List<Order> getAllOrders(Long memberId) {
        return null;
    }

    public Order getOrderDetailById(Long memberId, Long orderId) {
        return null;
    }

    public boolean updateOrder(Order order) {
        return true;
    }

    public boolean deleteOrder(Long memberId, Long orderId) {
        return false;
    }
}
