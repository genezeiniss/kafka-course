package com.genezeiniss.kafka.demo.configuration;

import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerProperties {

    // add necessary producer properties (https://kafka.apache.org/documentation/#producerconfigs)
    public static Properties producerProperties() {

        Properties properties = new Properties();
        properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        // following properties (serializers) help producer know what kind of parameters we send to kafka
        // and how to convert them to bytes
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer .class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // safe producer properties
        properties.setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true");
        // we don't need to set following configs, since they are implied automatically by enable.idempotence = true
        properties.setProperty(ACKS_CONFIG, "all"); // in log can be shown as acks = -1
        properties.setProperty(RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
        properties.setProperty(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        return properties;
    }
}
