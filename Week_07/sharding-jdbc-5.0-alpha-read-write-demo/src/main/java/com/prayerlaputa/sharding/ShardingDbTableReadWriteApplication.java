package com.prayerlaputa.sharding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot版 Sharding JDBC 分库分表+读写分离案例
 * 
 * @author chenglong.yu
 */
@SpringBootApplication
@EnableTransactionManagement
public class ShardingDbTableReadWriteApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShardingDbTableReadWriteApplication.class, args);
	}
}
