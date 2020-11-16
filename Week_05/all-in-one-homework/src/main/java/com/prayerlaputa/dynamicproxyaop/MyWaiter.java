package com.prayerlaputa.dynamicproxyaop;

/**
 * @author chenglong.yu
 * created on 2020/11/13
 */
public class MyWaiter implements Waiter {
    @Override
    public void service() {
        System.out.println("服务中");
    }
}
