package com.prayerlaputa.homework.nio.httpserver.netty;


public class NettyServerApplication {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(false,18808);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
