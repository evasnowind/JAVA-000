package com.prayerlaputa.jdbc;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
public class DBUtil2 {

    public static final String URL = "jdbc:mysql://localhost:3306/product_db";
    public static final String USER = "root";
    public static final String PASSWORD = "1qaz@WSX";

    public static void main(String[] args) throws Exception {
        //1.加载驱动程序
        Class.forName("com.mysql.jdbc.Driver");
        //2. 获得数据库连接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //3.操作数据库，实现增删改查
//        query(conn, 1);
//        insert(conn, 3, 30);
//        delete(conn, 3);
        update(conn, 1, 50);

        conn.close();
    }



    @SneakyThrows
    private static void query(Connection conn, Integer id) {
        String sql  = "select id, stock from product where id=?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();

        //如果有数据，rs.next()返回true
        while(rs.next()){
            System.out.println(rs.getInt("id")+" 库存："+rs.getInt("stock"));
        }
    }

    @SneakyThrows
    private static void insert(Connection conn, Integer id, Integer stock) {
        String sql = "insert product(id, stock) values(?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, stock);
        int rs = preparedStatement.executeUpdate();
        System.out.println(rs);
    }

    @SneakyThrows
    private static void delete(Connection conn, Integer id) {

        String sql = "delete from  product where id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        int rs = preparedStatement.executeUpdate();
        System.out.println(rs);
    }

    @SneakyThrows
    private static void update(Connection conn, Integer id, Integer stock) {
        String sql = "update product set stock=? where id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, stock);
        preparedStatement.setInt(2, id);
        int rs = preparedStatement.executeUpdate();
        System.out.println(rs);
    }

}
