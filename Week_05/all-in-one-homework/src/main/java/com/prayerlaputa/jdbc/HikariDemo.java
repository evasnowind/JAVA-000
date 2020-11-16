package com.prayerlaputa.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
public class HikariDemo {


    public static void main(String[] args) {

        //配置文件
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/product_db");//mysql
//        hikariConfig.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:orcl");//oracle
//        hikariConfig.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("1qaz@WSX");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {

            //创建connection
            conn = ds.getConnection();
            statement = conn.createStatement();

            //执行sql
            rs = statement.executeQuery("select * from product");

            //取数据
            if (rs.next()) {
                System.out.println(rs.getInt("id")+" 库存："+rs.getInt("stock"));
            }

            //关闭connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
