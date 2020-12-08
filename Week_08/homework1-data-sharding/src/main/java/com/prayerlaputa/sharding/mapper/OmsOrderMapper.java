package com.prayerlaputa.sharding.mapper;

import com.prayerlaputa.sharding.model.OmsOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenglong.yu
 * created on 2020/12/7
 */
@Mapper
public interface OmsOrderMapper {

    Long insertOrder(OmsOrder omsOrder);

    List<OmsOrder> listAll();

    List<OmsOrder> listByMemberId(Long memberId);

    OmsOrder getById(@Param("memberId") Long memberId, @Param("orderId") Long orderId);

    Long updateById(OmsOrder omsOrder);

    Long deleteById(@Param("memberId") Long memberId, @Param("orderId") Long orderId);
}
