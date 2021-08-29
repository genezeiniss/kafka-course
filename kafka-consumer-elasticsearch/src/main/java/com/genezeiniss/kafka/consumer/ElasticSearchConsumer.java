package com.genezeiniss.kafka.consumer;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchConsumer {

    private final ElasticSearchClient elasticSearchClient;
    Logger log = LoggerFactory.getLogger(ElasticSearchConsumer.class);

    public ElasticSearchConsumer(ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public static void main(String[] args) throws IOException {
        new ElasticSearchConsumer(new ElasticSearchClient()).run();
    }

    public void run() throws IOException {

        RestHighLevelClient client = elasticSearchClient.buildClient();
        String jsonString = "{\"foo\": \"bar\"}";

        IndexRequest indexRequest = new IndexRequest("twitter", "tweets")
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client
                .index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        log.info(id);

        // to close the client gracefully
        client.close();
    }

}
