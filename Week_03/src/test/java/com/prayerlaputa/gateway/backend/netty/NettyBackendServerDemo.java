package com.prayerlaputa.gateway.backend.netty;

import java.io.IOException;

/**
 * @author chenglong.yu
 * created on 2020/10/31
 */
public class NettyBackendServerDemo {
    public static void main(String[] args) {
        try {
            startNewThread(18805);
            startNewThread(18806);
            startNewThread(18807);
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startNewThread(final int port) {
        new Thread(() -> {
            HttpServer server = new HttpServer(false,port);
            try {
                server.run();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }, "thread-port-" + port).start();
    }
}
