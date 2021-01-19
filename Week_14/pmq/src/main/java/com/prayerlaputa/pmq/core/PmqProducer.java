package com.prayerlaputa.pmq.core;

public class PmqProducer {

    private PmqBroker broker;

    public PmqProducer(PmqBroker broker) {
        this.broker = broker;
    }

    public boolean send(String topic, PmqMessage message) {
        Pmq pmq = this.broker.findKmq(topic);
        if (null == pmq) {
            throw new RuntimeException("Topic[" + topic + "] doesn't exist.");
        }
        return pmq.send(message);
    }
}
