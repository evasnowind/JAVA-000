package com.prayerlaputa.jdbc;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author chenglong.yu
 * created on 2020/11/16
 */
public class DBUtil {

    public static final String URL = "jdbc:mysql://localhost:3306/product_db";
    public static final String USER = "root";
    public static final String PASSWORD = "1qaz@WSX";

    public static void main(String[] args) throws Exception {
        //1.加载驱动程序
        Class.forName("com.mysql.jdbc.Driver");
        //2. 获得数据库连接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        //3.操作数据库，实现增删改查
//        insert(conn, 3, 30);
//        delete(conn, 3);
        update(conn, 2, 100);

        conn.close();
    }



    @SneakyThrows
    private static void query(Connection conn) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, stock FROM product;");
        //如果有数据，rs.next()返回true
        while(rs.next()){
            System.out.println(rs.getInt("id")+" 库存："+rs.getInt("stock"));
        }
    }

    @SneakyThrows
    private static void insert(Connection conn, Integer id, Integer stock) {
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate("insert product(id, stock) values(" + id + ", " + stock +");");
        System.out.println(rs);
    }

    @SneakyThrows
    private static void delete(Connection conn, Integer id) {
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate("delete from product where id=" + id + ";");
        System.out.println(rs);
    }

    @SneakyThrows
    private static void update(Connection conn, Integer id, Integer stock) {
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate("update product set stock=" + stock + " where id=" + id + ";");
        System.out.println(rs);
    }

}
