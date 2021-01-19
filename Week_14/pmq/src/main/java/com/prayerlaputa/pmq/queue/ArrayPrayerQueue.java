package com.prayerlaputa.pmq.queue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chenglong.yu
 * created on 2021/1/18
 */
public class ArrayPrayerQueue implements PrayerQueue {

    private boolean debugMode = false;

    private int capacity;
    private Object[] queue;
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    private int header, tail;

    private final AtomicInteger counter = new AtomicInteger();

    public ArrayPrayerQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new Object[capacity];
        header = 0;
        tail = 0;
    }

    @Override
    public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
        Object res = null;

        String currentThread = Thread.currentThread().getName();
        if (debugMode) {
            System.out.println(currentThread + " enter poll");
        }


        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.counter;
        final ReentrantLock localTakeLock = this.takeLock;
        localTakeLock.lockInterruptibly();
        try {
            while(count.get() == 0) {
                if (nanos <= 0) {
                    /*
                    如果已结束等待时间，但还是没有任何元素，则直接返回null
                     */
                    return null;
                }
                /*
                awaitNanos返回的是剩余需要等待的时间。
                看源码注释可知，该方法可能会阻塞，直到如下场景才会被唤醒：
                - 别的线程调用了该Condition的signal/signalAll方法，然后本线程被唤醒
                - 别的线程interrupt本线程
                - 已睡了nanos时间，自动唤醒
                - 虚假唤醒 spurious wakeup
                 */
                if (debugMode) {
                    System.out.println(currentThread + " start await");
                }

                nanos = notEmpty.awaitNanos(nanos);

                if (debugMode) {
                    System.out.println(currentThread + " end await");
                }
            }

            res = dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                /*
                如果当前队列中不为空，则发通知，告知其他线程当前
                可以继续拉取。
                由于是先get然后increment，所以只有大于1才说明
                本线程拉取一个元素后，队列仍不会是空的。
                 */
                notEmpty.signal();
            }
        } finally {
            localTakeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }

        if (debugMode) {
            System.out.println(currentThread + " end poll, header=" + header + " tail=" + tail + " counter=" + counter.get());
        }
        return res;
    }

    @Override
    public Object poll() {
        Object res = null;

        String currentThread = Thread.currentThread().getName();
        System.out.println(currentThread + " enter poll");

        int c = -1;
        final AtomicInteger count = this.counter;
        final ReentrantLock localTakeLock = this.takeLock;
        localTakeLock.lock();
        try {
            if (count.get() > 0) {

                res = dequeue();
                c = count.getAndDecrement();
                if (c > 1) {
                /*
                如果当前队列中不为空，则发通知，告知其他线程当前
                可以继续拉取。
                由于是先get然后increment，所以只有大于1才说明
                本线程拉取一个元素后，队列仍不会是空的。
                 */
                    notEmpty.signal();
                }
            }
        } finally {
            localTakeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }

        System.out.println(currentThread + " end poll, header=" + header + " tail=" + tail + " counter=" + counter.get());
        return res;
    }


    @Override
    public boolean offer(Object o) {
        if (null == o) {
            throw new NullPointerException();
        }


        String currentThread = Thread.currentThread().getName();
        if (debugMode) {
            System.out.println(currentThread + " enter offer");
        }

        final AtomicInteger counter = this.counter;
        /*
        保存数据的队列已满，直接返回false
         */
        if (counter.get() == capacity) {
            return false;
        }
        int c = -1;
        final ReentrantLock localPutLock = this.putLock;
        localPutLock.lock();
        try {
            /*
            多线程调用时，可能出现：
            1、A线程执行到 localPutLock.lock()后，拿到锁，发生CPU调度，暂停
            2、B线程开始执行，在localPutLock.lock()阻塞，发生CPU调度
            3、A线程继续执行，给队列添加元素，假设添加成功后counter=capacity
            因此在下面这个添加逻辑必须额外加一句  if (counter.get() < capacity) 判断
             */
            if (counter.get() < capacity) {
                enqueue(o);
                c = counter.getAndIncrement();
                if (c + 1 < capacity) {
                    /*
                    此处暂时没实现offer(Object o,  long timeout, TimeUnit unit) 接口，
                    但参考LinkedBlockingQueue可以知道，必须在此处通知当前队列还不满、
                    可以继续加元素
                     */
                    notFull.signal();
                }
            }
        } finally {
            localPutLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }

        if (debugMode) {
            System.out.println(currentThread + " end offer, header=" + header + " tail=" + tail + " counter=" + counter.get());
        }
        return c >= 0;
    }

    /**
     * 调用enqueue时，必然已经加锁，enqueue方法
     * 本身不用考虑线程安全问题，直接操作即可。
     *
     * 另一方面，由于在offer()方法中，在调用enqueue方法前
     * 会判断队列是否已满，这保证了使用循环队列时，入队操作
     * 更新tail时，不会将tail移动到还在使用的数组索引。
     *
     * @param e
     */
    private void enqueue(Object e) {
        if (capacity <= 0) {
            throw new NullPointerException("没有足够的存储空间！");
        }

        queue[tail] = e;
        tail = (tail + 1) % capacity;
    }

    /**
     * 调用dequeue()时，必然已经加锁，deuque方法本身
     * 不用考虑线程安全问题。
     *
     * 另一方面，由于在poll()方法中，调用dequeue方法前
     * 会判断队列是否已空，保证dequeue操作更新header时，
     * 不会将其更新到还在使用的数组索引。
     *
     * @return
     */
    private Object dequeue() {
        if (counter.get() <= 0) {
            throw new NullPointerException("当前队列为空！");
        }
        final Object obj = queue[header];
        header = (header + 1) % capacity;
        return obj;
    }


    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }
}
