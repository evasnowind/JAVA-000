package com.prayerlaputa.queue;

import com.prayerlaputa.pmq.queue.ArrayPrayerQueue;
import com.prayerlaputa.pmq.queue.PrayerQueue;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2021/1/19
 */
@Slf4j
public class PrayerQueueTests {

    @Test
    public void testArrayPrayerQueue() {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(4);

        PrayerQueue pmq = new ArrayPrayerQueue(5);

        final int threadCnt = 2;
        final int msgCnt = 10;

        for (int i = 0; i < threadCnt; i++) {
            new Thread(
                    () -> {
//                        try {
//                            cyclicBarrier.await();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (BrokenBarrierException e) {
//                            e.printStackTrace();
//                        }
                        String threadName = Thread.currentThread().getName();
                        for (int j = 0; j < msgCnt; j++) {
                            try {
                                String msg = (String) pmq.poll(5, TimeUnit.SECONDS);
                                System.out.println(threadName + " poll msg:" + msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, "poll-thread-" + i
            ).start();
        }

        for (int i = 0; i < threadCnt; i++) {
            new Thread(() -> {
//                try {
//                    cyclicBarrier.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (BrokenBarrierException e) {
//                    e.printStackTrace();
//                }
                String threadName = Thread.currentThread().getName();
                for (int j = 0; j < msgCnt; j++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String msg = threadName + "-" + j;
                    System.out.println("offer msg:" + msg);
                    pmq.offer(msg);
                }
            }, "offer-thread-" + i).start();
        }

//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();

        try {
            System.out.println("finish all!");
            Thread.sleep(5000);
//            System.in.read();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
