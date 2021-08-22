package com.genezeiniss.kafka.demo.producer;

import com.genezeiniss.kafka.configuration.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

        // 1. define producer properties
        Properties properties = ProducerProperties.producerProperties();

        // 2. create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i <10; i ++) {
            // 3. create producer record
            ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", String.format("hello world %s", i));

            //4. send data with callback
            producer.send(record, (metadata, exception) -> {

                if (exception == null) {
                    log.info(String.format("received new metadata. \n" +
                            "topic: %s \n" +
                            "partition: %s \n" +
                            "offset: %s \n" +
                            "timestamp: %s", metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp()));
                } else {
                    log.error("error while producing", exception);
                }

            });
        }

        // 5. flush data using producer.flush() or flush and close producer using producer.close()
        producer.flush();
        producer.close();
    }
}
