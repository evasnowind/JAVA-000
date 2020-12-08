package com.prayerlaputa.sharding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot版 Sharding JDBC 分库分表案例
 * 
 * @author chenglong.yu
 */
@SpringBootApplication
public class ShardingDbTableReadWriteApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShardingDbTableReadWriteApplication.class, args);
	}
}
