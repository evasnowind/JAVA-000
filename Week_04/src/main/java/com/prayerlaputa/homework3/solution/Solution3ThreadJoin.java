package com.prayerlaputa.homework3.solution;

import java.util.concurrent.locks.LockSupport;

/**
 * @author chenglong.yu
 * created on 2020/11/9
 */
public class Solution3ThreadJoin {


    private static int result = 0;


    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        Thread joinThread = new Thread(() -> {
            result = sum();
        }, "ThreadJoin");
        joinThread.start();

        System.out.println("主线程等待中...");
        try {
            /*
            使用join()方法，使得主线程让出CPU时间，等计算joinThread执行结束后才
             */
            joinThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 确保  拿到result 并输出
        System.out.println("阻塞结束，异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

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
