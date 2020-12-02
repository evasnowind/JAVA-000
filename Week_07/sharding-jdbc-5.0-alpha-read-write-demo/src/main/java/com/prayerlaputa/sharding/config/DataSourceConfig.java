package com.prayerlaputa.sharding.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenglong.yu
 * created on 2020/12/2
 */
@Configuration
@MapperScan(basePackages = "com.prayerlaputa.sharding.repository")
@ImportAutoConfiguration(MybatisAutoConfiguration.class)
public class DataSourceConfig {
}
