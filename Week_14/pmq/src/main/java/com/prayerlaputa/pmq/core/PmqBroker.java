package com.prayerlaputa.pmq.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PmqBroker {

    public static final int CAPACITY = 10000;

    private final Map<String, Pmq> kmqMap = new ConcurrentHashMap<>(64);

    public void createTopic(String name){
        kmqMap.putIfAbsent(name, new Pmq(name,CAPACITY));
    }

    public Pmq findKmq(String topic) {
        return this.kmqMap.get(topic);
    }

    public PmqProducer createProducer() {
        return new PmqProducer(this);
    }

    public PmqConsumer createConsumer() {
        return new PmqConsumer(this);
    }

}
