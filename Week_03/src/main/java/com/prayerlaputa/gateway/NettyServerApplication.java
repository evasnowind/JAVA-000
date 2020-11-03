package com.prayerlaputa.gateway;


import com.prayerlaputa.gateway.inbound.HttpInboundServer;

import java.util.Arrays;
import java.util.List;

public class NettyServerApplication {
    
    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";
    
    public static void main(String[] args) {
//        String proxyServer = System.getProperty("proxyServer","http://localhost:18807");
        List<String> proxyServerList = Arrays.asList("http://localhost:18805","http://localhost:18806", "http://localhost:18807");
        String proxyPort = System.getProperty("proxyPort","18808");
        
          //  http://localhost:8888/api/hello  ==> gateway API
          //  http://localhost:8088/api/hello  ==> backend service
    
        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServerList);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port + " for server:" + proxyServerList);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
