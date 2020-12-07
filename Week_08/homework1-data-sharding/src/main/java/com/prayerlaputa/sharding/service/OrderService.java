package com.prayerlaputa.sharding.service;

import com.prayerlaputa.sharding.mapper.OrderMapper;
import com.prayerlaputa.sharding.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenglong.yu
 * created on 2020/12/7
 */
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public void insertOrder(Order order) {

    }

    public boolean deleteById(Long memberId, Long orderId) {
        return false;
    }

    public Order getOrderById(Long memberId, Long orderId) {
        return orderMapper.getById(memberId, orderId);
    }

    public boolean updateOrder(Order order) {
        return false;
    }

}
