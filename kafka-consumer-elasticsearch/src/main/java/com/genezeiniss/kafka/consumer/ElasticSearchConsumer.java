package com.genezeiniss.kafka.consumer;

import com.genezeiniss.kafka.configuration.ConsumerProperties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ElasticSearchConsumer {

    private final ElasticSearchClient elasticSearchClient;
    Logger log = LoggerFactory.getLogger(ElasticSearchConsumer.class);

    public ElasticSearchConsumer(ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public static KafkaConsumer<String, String> createConsumer(List<String> topics) {
        Properties properties = ConsumerProperties.consumeProperties("kafka-elasticsearch");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        // subscribe to list of topics
        consumer.subscribe(topics);
        return consumer;
    }

    public static void main(String[] args) {
        new ElasticSearchConsumer(new ElasticSearchClient()).run();
    }

    public void run() {

        RestHighLevelClient client = elasticSearchClient.buildClient();

        KafkaConsumer<String, String> consumer = createConsumer(Collections.singletonList("twitter_tweets"));
        // poll for new data
        while (true) {
            consumer.poll(Duration.ofMillis(100))
                    .forEach(record -> {
                        // here we insert data into elasticsearch
                        IndexRequest indexRequest = new IndexRequest("twitter", "tweets")
                                .source(record.value(), XContentType.JSON);

                        IndexResponse indexResponse = null;
                        try {
                            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                            log.info(indexResponse.getId());
                            Thread.sleep(1000);
                        } catch (Exception exception) {
                            log.error(exception.getMessage());
                        }
                    });
        }
    }
}
