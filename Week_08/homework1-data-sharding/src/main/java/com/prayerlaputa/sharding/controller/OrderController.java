package com.prayerlaputa.sharding.controller;

import com.prayerlaputa.sharding.model.OmsOrder;
import com.prayerlaputa.sharding.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author chenglong.yu
 * created on 2020/12/7
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @GetMapping("/add")
    public boolean insertOrder(String username, Long memberId, BigDecimal payment) {

        OmsOrder order = new OmsOrder();
        order.setMemberId(memberId);
        order.setCouponId(ThreadLocalRandom.current().nextLong(100));
        order.setOrderSn(UUID.randomUUID().toString());
        Date cur = new Date();
        order.setCreateTime(cur);
        order.setMemberUsername(username);
        order.setTotalAmount(payment);
        order.setPayAmount(payment);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPromotionAmount(BigDecimal.ZERO);
        order.setIntegrationAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceiverName(username);
        order.setReceiverPhone("15811111111");
        orderService.insertOrder(order);

        return true;
    }

    @GetMapping("/listByMemberId")
    public List<OmsOrder> getOrderByMemberId(Long memberId) {
        return orderService.getOrderByMemberId(memberId);
    }

    @GetMapping("/list")
    public List<OmsOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/get")
    public OmsOrder getOrderDetailById(Long memberId, Long orderId) {
        return orderService.getOrderById(memberId, orderId);
    }

    /**
     * 更新操作
     * 为了测试方便，就写了一个根据memberId orderId更新订单总金额的接口。
     *
     * @param memberId
     * @param orderId
     * @param amount
     * @return
     */
    @GetMapping("/update/amount")
    public boolean updateOrder(Long memberId, Long orderId, BigDecimal amount) {
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setMemberId(memberId);
        order.setTotalAmount(amount);
        order.setPayAmount(amount);
        return orderService.updateOrder(order);
    }


    @GetMapping("/delete")
    public boolean deleteOrder(Long memberId, Long orderId) {
        return orderService.deleteById(memberId, orderId);
    }
}
