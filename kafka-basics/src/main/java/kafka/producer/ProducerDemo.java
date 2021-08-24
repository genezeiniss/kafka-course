package kafka.producer;

import kafka.configuration.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerDemo {

    public static void main(String[] args) {

        // 1. define producer properties
        Properties properties = ProducerProperties.producerProperties();

        // 2. create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // 3. create producer record
        ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "hello world");

        //4. send data - asynchronous
        producer.send(record);

        // 5. flush data using producer.flush() or flush and close producer using producer.close()
        producer.flush();
        producer.close();
    }
}
