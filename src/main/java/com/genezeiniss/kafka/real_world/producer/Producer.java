package com.genezeiniss.kafka.real_world.producer;

import com.genezeiniss.kafka.configuration.ProducerProperties;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class Producer {

    public Producer(){}

    public KafkaProducer<String, String> createKafkaProducer() {
        Properties properties = ProducerProperties.producerProperties();
        return new KafkaProducer<>(properties);
    }
}
