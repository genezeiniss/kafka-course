package com.genezeiniss.kafka.demo.concumer;

import com.genezeiniss.kafka.configuration.ConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/** assign and seek are mostly used to replay data or fetch a specific message */
public class ConsumerDemoAssignSeek {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);

        Properties properties = ConsumerProperties.consumeProperties(null);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // assign
        TopicPartition partitionToReadFrom = new TopicPartition("first_topic", 0);
        consumer.assign(Collections.singleton(partitionToReadFrom));

        // seek
        long offsetToReadFrom = 15;
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        // poll for new data
        int messagesToRead = 5;
        int messagesReadSoFar = 0;
        boolean keepReading = true;

        while(keepReading) {

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, String> record : records) {
                log.info("key {}, value {}, partition {}, offsets {}",
                        record.key(), record.value(), record.partition(), record.offset());
                messagesReadSoFar += 1;
                if (messagesReadSoFar >= messagesToRead) {
                    keepReading = false; // to exist while loop
                    break; // to exist for loop
                }
            }
        }
    }
}
