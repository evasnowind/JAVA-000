package com.prayerlaputa.pmq.queue;

import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 * created on 2021/1/18
 */
public interface PrayerQueue<E> {


    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    E poll();

    boolean offer(E e);
}
