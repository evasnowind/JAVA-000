package com.prayerlaputa.sharding.mapper;

import com.prayerlaputa.sharding.model.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/12/7
 */
@Mapper
public interface OrderMapper {

    Long insertOrder(Order order);

    List<Order> list();

    Order getById(Long memberId, Long orderId);

    Long updateById(Order order);

    Long deleteById(Long memberId, Long order);
}
