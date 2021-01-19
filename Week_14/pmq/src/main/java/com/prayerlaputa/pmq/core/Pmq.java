package com.prayerlaputa.pmq.core;

import com.prayerlaputa.pmq.queue.ArrayPrayerQueue;
import com.prayerlaputa.pmq.queue.PrayerQueue;
import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class Pmq {

    public Pmq(String topic, int capacity) {
        this.topic = topic;
        this.capacity = capacity;
//        this.queue = new LinkedBlockingQueue(capacity);
        this.queue = new ArrayPrayerQueue(capacity);
    }

    private String topic;

    private int capacity;

//    private LinkedBlockingQueue<PmqMessage> queue;

    private PrayerQueue<PmqMessage> queue;

    public boolean send(PmqMessage message) {
        return queue.offer(message);
    }

    public PmqMessage poll() {
        return queue.poll();
    }

    @SneakyThrows
    public PmqMessage poll(long timeout) {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

}
