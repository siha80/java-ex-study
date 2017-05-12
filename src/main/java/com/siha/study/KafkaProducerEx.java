package com.siha.study;

import kafka.utils.VerifiableProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class KafkaProducerEx {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "pri-dev-kafka-check.okcashbag.com:9091,pri-dev-kafka-check.okcashbag.com:9092,pri-dev-kafka-check.okcashbag.com:9093");
//        props.put("partitioner.class", RoundRobinPartitioner.class.getName());

        StringSerializer stringSerializer = new StringSerializer();
        for(int i = 0; i < 1; i++) {
            Producer<String, String> producer = new KafkaProducer(props, stringSerializer, stringSerializer);
            producer.send(new ProducerRecord<String, String>("ocbcheck", "test", String.format("Hello, world..OcbCheck...[%d]", i)));
            producer.close();
        }
    }
}
