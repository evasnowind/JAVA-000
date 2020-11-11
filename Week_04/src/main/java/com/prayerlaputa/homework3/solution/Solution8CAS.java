package com.prayerlaputa.homework3.solution;

import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2020/11/9
 */
public class Solution8CAS {


    private static volatile int result = 0;

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法


        new Thread(() -> {
            result = sum();
        }, "WaitNotify").start();

        try {
            System.out.println("主线程等待中...");
            while(0 == result) {
                /*
                使用CAS的思想，空转一下
                 */
                TimeUnit.MILLISECONDS.sleep(10);
            }

            // 确保  拿到result 并输出
            System.out.println("阻塞结束，异步计算结果为：" + result);
            System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 退出main线程
    }


    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2) {
            return 1;
        }

        return fibo(a - 1) + fibo(a - 2);
    }
}
