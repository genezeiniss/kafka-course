package kafka.producer;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TwitterClient {

    private final String consumerKey = "EjzIRW8Zb7lIlZCMlooLw6eTY";
    private final String consumerSecret = "t8E7tf4beuQeq6VyNaRffqC5OHQTl6CiQwiGFyTcPCQ8AUQih6";
    private final String token = "1067768825532289029-hGyfLOI3USRMcPN1gdka2HQkGXYRzx";
    private final String secret = "IHBahY2VjuLNtpkYEVVK8K8QLATWE1qlCSGsCNAXx0jtz";

    public TwitterClient() {
    }

    public Client buildClient(BlockingQueue<String> messageQueue) {
        return new ClientBuilder()
                .name("Twitter-Client-01")
                .hosts(new HttpHosts(Constants.STREAM_HOST))
                .authentication(new OAuth1(consumerKey, consumerSecret, token, secret))
                .endpoint(setupTrackTerms())
                .processor(new StringDelimitedProcessor(messageQueue))
                .build();
    }

    private StatusesFilterEndpoint setupTrackTerms() {
        List<String> terms = Lists.newArrayList("java", "kafka", "bitcoin", "politics", "sport");
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(terms);
        return endpoint;
    }
}
