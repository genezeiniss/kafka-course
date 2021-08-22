package com.genezeiniss.kafka.demo.producer;

import com.genezeiniss.kafka.demo.configuration.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/** By providing a key we guarantee, the same key is always going to the same partition
 * */
public class ProducerDemoKeys {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger log = LoggerFactory.getLogger(ProducerDemoKeys.class);

        // 1. define producer properties
        Properties properties = ProducerProperties.producerProperties();

        // 2. create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i <10; i ++) {
            // 3. create producer record
            String topic = "first_topic";
            String key = String.format("id_%s", i);
            String value = String.format("hello world %s", i);

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            log.info("key: {}", key);

            //4. send data synchronous (bad practice)
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

            }).get(); // block the .send() to make it synchronous - don't do this in production!
        }

        // 5. flush data using producer.flush() or flush and close producer using producer.close()
        producer.flush();
        producer.close();
    }

//    id_0 - partition: 1
//    id_1 - partition: 0
//    id_2 - partition: 2
//    id_3 - partition: 0
//    id_4 - partition: 2
//    id_5 - partition: 2
//    id_6 - partition: 0
//    id_7 - partition: 2
//    id_8 - partition: 1
//    id_9 - partition: 2
}
