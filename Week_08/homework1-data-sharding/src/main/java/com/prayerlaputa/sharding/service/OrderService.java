package com.prayerlaputa.sharding.service;

import com.prayerlaputa.sharding.mapper.OmsOrderMapper;
import com.prayerlaputa.sharding.model.OmsOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/12/7
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    public Long insertOrder(OmsOrder omsOrder) {
        return omsOrderMapper.insertOrder(omsOrder);
    }

    public boolean deleteById(Long memberId, Long orderId) {
         Long deletedCnt = omsOrderMapper.deleteById(memberId, orderId);
         return null != deletedCnt && deletedCnt > 0 ? true : false;
    }

    public List<OmsOrder> getAllOrders() {
        return omsOrderMapper.listAll();
    }

    public OmsOrder getOrderById(Long memberId, Long orderId) {
        OmsOrder order = omsOrderMapper.getById(memberId, orderId);
        log.info("getOrderById order={}.", order);
        return order;
    }

    public List<OmsOrder> getOrderByMemberId(Long memberId) {
        return omsOrderMapper.listByMemberId(memberId);
    }

    public boolean updateOrder(OmsOrder omsOrder) {
        Long updateCnt = omsOrderMapper.updateById(omsOrder);
        return null != updateCnt && updateCnt > 0 ? true : false;
    }

}
