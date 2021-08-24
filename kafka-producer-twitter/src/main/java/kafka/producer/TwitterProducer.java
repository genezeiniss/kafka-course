package kafka.producer;

import com.twitter.hbc.core.Client;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    private final TwitterClient twitterClient;
    private final Producer producer;

    public TwitterProducer(TwitterClient twitterClient, Producer kafkaProducer) {
        this.twitterClient = twitterClient;
        this.producer = kafkaProducer;
    }

    Logger log = LoggerFactory.getLogger(TwitterProducer.class);

    public void run() {

        // create a twitter client
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(100000);
        Client client = twitterClient.buildClient(messageQueue);
        client.connect();

        // create a kafka producer
        KafkaProducer<String, String> producer = this.producer.createKafkaProducer();

        // add shutdown hook
        shutdownHook(client, producer);

        // loop a tweets to kafka
        while (!client.isDone()) {
            pollMessage(messageQueue)
                    .onSuccess(tweet -> {
                        log.info("the tweet is: {}", tweet);
                        sendMessage(producer, tweet);
                    })
                    .onFailure(throwable -> {
                        log.info("something went wrong", throwable);
                        client.stop();
                    });
        }
        log.info("end of application");
    }

    private void shutdownHook(Client client, KafkaProducer<String, String> producer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("is about to stop application");
            log.info("shutting down client from twitter...");
            client.stop();

            //close() on producer sends all messages in memories of the producer to kafka application before it's shut down
            log.info("closing producer...");
            producer.close();

            log.info("application is closed");
        }));
    }

    private Try<String> pollMessage(BlockingQueue<String> messageQueue) {
        return Try.of(() -> messageQueue.poll(5, TimeUnit.SECONDS));
    }

    private void sendMessage(KafkaProducer<String, String> producer, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>("twitter_tweets", null, message);
        producer.send(record, (metadata, exception) ->
                Optional.ofNullable(exception).ifPresent(ex -> log.error("something went wrong", ex)));
    }

    public static void main(String[] args) {
        new TwitterProducer(new TwitterClient(), new Producer()).run();
    }
}
