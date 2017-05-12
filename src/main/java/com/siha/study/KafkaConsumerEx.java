package com.siha.study;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaConsumerEx {
    public static void main(String[] args) {
        ConsumerExample example = new ConsumerExample();
        example.run();

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ie) {
//
//        }
//        example.shutdown();
    }

    public static class ConsumerExample {
        ExecutorService executor;
        ConsumerConnector consumer;

        public ConsumerExample() {
            executor = Executors.newFixedThreadPool(5);
            Properties props = new Properties();
            props.put("zookeeper.connect", "pri-dev-zookeeper-check.okcashbag.com:2181,pri-dev-zookeeper-check.okcashbag.com:2182,pri-dev-zookeeper-check.okcashbag.com:2183");
            props.put("zookeeper.session.timeout.ms", "400");
            props.put("zookeeper.sync.time.ms", "200");
            props.put("group.id", "group");
            props.put("auto.commit.interval.ms", "1000");

            consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        }

        public void run() {
            List<KafkaStream<byte[], byte[]>> streams = consumer.createMessageStreams(
                    new HashMap<String, Integer>() {{
                        put("kafkatopic", 5);
                    }}
            ).get("kafkatopic");

            streams.stream()
                    .forEach(s -> executor.submit(
                            new Thread(() -> {
                                ConsumerIterator<byte[], byte[]> it = s.iterator();
                                while (it.hasNext()) {
                                    log.info("Received: {}", new String(it.next().message()));
                                }
                                log.info("Shutting down Thread...");
                            }))
                    );
        }

        public void shutdown() {
            if (consumer != null) consumer.shutdown();
            if (executor != null) executor.shutdown();
            try {
                if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                    System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted during shutdown, exiting uncleanly");
            }
        }
    }
}
