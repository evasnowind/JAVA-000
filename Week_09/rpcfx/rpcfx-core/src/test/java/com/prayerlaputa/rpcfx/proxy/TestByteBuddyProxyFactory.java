package com.prayerlaputa.rpcfx.proxy;

import com.prayerlaputa.rpcfx.client.proxy.ByteBuddyProxyFactory;
import com.prayerlaputa.rpcfx.common.RpcException;

/**
 * @author chenglong.yu
 * created on 2020/12/14
 */
public class TestByteBuddyProxyFactory {


    public static void main(String[] args) {
        ByteBuddyProxyFactory factory = new ByteBuddyProxyFactory();
        try {
            HelloService service = factory.createProxy(HelloService.class, "http://www.baidu.com");
            String res = service.sayHello("hello world!");
            System.out.println(res);
        } catch (RpcException e) {
            e.printStackTrace();
        }
    }
}
