package kafka.concumer;

import kafka.configuration.ConsumerProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(ConsumerDemo.class);

        Properties properties = ConsumerProperties.consumeProperties("my-first-application");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // subscribe to list of topics
        consumer.subscribe(Collections.singleton("first_topic"));

        // poll for new data
        while(true) {
            // returns ConsumerRecords<String, String>
            consumer.poll(Duration.ofMillis(100))
                    .forEach(record -> log.info("key {}, value {}, partition {}, offsets {}",
                            record.key(), record.value(), record.partition(), record.offset()));
        }
    }
}
