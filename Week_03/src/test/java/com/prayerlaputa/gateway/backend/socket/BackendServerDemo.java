package com.prayerlaputa.gateway.backend.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * TODO 这个简单的socket例子，作为后端服务时，网关转发请求操作无法结束，初步推断是
 * 因为没有写上Content-Length？有待进一步调试。
 *
 * @author chenglong.yu
 * created on 2020/10/30
 */
public class BackendServerDemo {

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        ServerSocket serverSocket = new ServerSocket(18084);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> {
                    service(socket);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void service(Socket socket) {
        try {
            Thread.sleep(20);

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            printWriter.println();
            printWriter.write("hello, nio!");
            printWriter.close();
            socket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
