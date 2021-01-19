package com.prayerlaputa.pmq.core;

public class PmqConsumer<T> {

    private final PmqBroker broker;

    private Pmq pmq;

    public PmqConsumer(PmqBroker broker) {
        this.broker = broker;
    }

    public void subscribe(String topic) {
        this.pmq = this.broker.findKmq(topic);
        if (null == pmq) {
            throw new RuntimeException("Topic[" + topic + "] doesn't exist.");
        }
    }

    public PmqMessage<T> poll(long timeout) {
        return pmq.poll(timeout);
    }

}
