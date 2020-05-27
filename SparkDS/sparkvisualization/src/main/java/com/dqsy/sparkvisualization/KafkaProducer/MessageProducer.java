package com.dqsy.sparkvisualization.KafkaProducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName MessageProducer.java
 * @Description TODO
 * @createTime 2020年04月23日 15:24:00
 */
@Slf4j
public class MessageProducer {
    private static final String TOPIC = "AdRealTimeLog";
    private static final String BROKER_LIST = "master:9092,slave1:9092,slave2:9092";
    private static KafkaProducer<Integer, String> producer = null;

    private static Properties initConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    public void run(String message) {
        Properties configs = initConfig();
        producer = new KafkaProducer<Integer, String>(configs);
        try {
            ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, message);
            producer.send(record);
            log.info("消息发送成功..." + message);
        } catch (Exception e) {
            log.error("消息发送失败..." + message);
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
