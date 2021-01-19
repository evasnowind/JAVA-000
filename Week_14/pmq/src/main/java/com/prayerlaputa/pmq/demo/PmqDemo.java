package com.prayerlaputa.pmq.demo;

import com.prayerlaputa.pmq.core.PmqBroker;
import com.prayerlaputa.pmq.core.PmqConsumer;
import com.prayerlaputa.pmq.core.PmqMessage;
import com.prayerlaputa.pmq.core.PmqProducer;
import lombok.SneakyThrows;

public class PmqDemo {

    @SneakyThrows
    public static void main(String[] args) {

        String topic = "kk.test";
        PmqBroker broker = new PmqBroker();
        broker.createTopic(topic);

        PmqConsumer consumer = broker.createConsumer();
        consumer.subscribe(topic);
        final boolean[] flag = new boolean[1];
        flag[0] = true;
        new Thread(() -> {
            while (flag[0]) {
                PmqMessage<Order> message = consumer.poll(100);
                if(null != message) {
                    System.out.println(message.getBody());
                }
            }
            System.out.println("程序退出。");
        }).start();

        PmqProducer producer = broker.createProducer();
        for (int i = 0; i < 100; i++) {
            Order order = new Order(1000L + i, System.currentTimeMillis(), "USD2CNY", 6.51d);
            order.getId();
            producer.send(topic, new PmqMessage(null, order));
        }
        Thread.sleep(500);
        System.out.println("点击任何键，发送一条消息；点击q或e，退出程序。");
        while (true) {
            char c = (char) System.in.read();
            if(c > 20) {
                System.out.println(c);
                producer.send(topic, new PmqMessage(null, new Order(100000L + c, System.currentTimeMillis(), "USD2CNY", 6.52d)));
            }

            if( c == 'q' || c == 'e') break;
        }

        flag[0] = false;

    }
}
