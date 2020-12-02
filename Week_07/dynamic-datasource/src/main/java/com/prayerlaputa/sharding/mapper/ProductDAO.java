package com.prayerlaputa.sharding.mapper;

import com.prayerlaputa.sharding.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author chenglong.yu
 * created on 2020/12/1
 */
@Mapper
public interface ProductDAO {

    Product select(@Param("id") long id);

    Integer update(Product product);

    Integer insert(Product product);

    Integer delete(long productId);
}
