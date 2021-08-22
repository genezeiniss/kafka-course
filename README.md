###To start working on CLI:
1. start __zookeeper__:
   1. $ cd kafka_2.13-2.8.0
   2. $ bin/zookeeper-server-start.sh config/zookeeper.properties
2. start __kafka__ in a new window 
   1. $kafka-server-start.sh config/server.properties

#Kafka Theory
##Topics, Partitions and Offsets
__topic::partition::offset__

###Topic →
1. a particular stream of data.
   1. similar to a table in database (without constraints)
   2. a topic is identified by its name.
2. topics are split in partitions
3. when creating a topic, we should decide how many partitions it will include. These settings are changeable.

###Partitions →
1. each partition is ordered
2. each message within a partition gets an incremental id, called offset.

###Offset → 
1. an incremental id of message in the partition (starting by 0).
Incremental, meaning that each successive insert into the partition will automatically assign a value greater than any previous message of the partition.
   1. offset guarantees an order within a partition.
   2. offset 1 will never be read before offset 0.
   3. offset only has a meaning in a specific partition (not across partitions).

#### Notes:
→ Data is kept only for a limited time (default one week)

→ Once data is written to partition → it can’t be changed (immutability)

→ Data is assigned randomly to a partition, unless a key is provided (wha?)

##Brokers
__broker::topic::partition::offset__

What holds the topics? The answer is → Brokers. 

Kafka cluster (multiple machine) is composed of multiple brokers (servers).

If one broker is down → another one will serve the data.

###Broker →
1. each broker is identified with numeric id
   1. arbitrary
   2. must be integer
2. each broker contains a certain topic’s partitions (not all the data), because kafka is distributed
   1. a distributed system, also known as distributed computing, is a system with multiple components located on different machines that communicate and coordinate actions in order to appear as a single coherent system to the end-user.
3. when you connected to any broker (bootstrap broker) → you connected to entire cluster (even if it had dozens brokers)
4. the best practice is to start with 3 brokers (but cluster can be enlarged and contain over 100 brokers)

###Replication →
The goal of a distributed system is to allow data replication.
It means, topics should have a replication factor (best practices is 3)
1. replication factor 3 means that you will have 3 copies of data (located in different brokers).
2. in kafka you cannot create a replication factor greater than available brokers.

####Notes:
→ At any time, only ONE broker can be a LEADER for a given partition.

→ Only that leader can receive and set data for the partition.


#Producer
## Producer Configuration
### Asks (Acknowledgments)
_asks = 0_ 
1. no response required (broker never replies to producer)
2. if the broker goes offline, or an exception happen → we won't know, and we will lose data.
3. useful when it's ok to potentially lose data.
   1. metrics collection
   2. log collection
   
_asks = 1 (default: leader acknowledgments)_
1. leader response to every write request is requested; but replication response is not guarantee (happens in background)
2. if an ack is not received, the producer may retry.
3. if leader broker goes offline, but replicas haven't replicated the data yet → we will lose data.

_asks = all (replicas acknowledgments)_
1. leader and replicas responses required to every write request.
2. added latency, since now producer wait to all replicates response to do their replication
3. added safety, since the requests are more guarantee. There is no data lose (if we have enough replicas)
4. this is __necessary__ setting if no data lose is required.

asks all __must__ be used with additional configuration parameter → _min.insync.replicas_, which can be set at a broker, or a topic level (override).

__Note__: __asks = all__ in a higher kafka versions are shown as __acks = -1__

_min.insync.replicas = 2 (most common setting)_
1. at least two brokers, that are ISR (in-sync replica), including LEADER, must respond that they have the data, otherwise you will have an exception message
2. example: if _replication.factor = 1_, _min.insync.replicas = 2_, _asks = all_ → you can only tolerate one broker is going down,
otherwise the producer will receive exception.
   
### Retries
When we have failures, such as transient failures, in a producer, the developers are expecting to analyze an exceptions, otherwise the data will be lost (since it will not be resent).
1. transient failures example:
   1. __NotEnoughReplicaException__
   
2. retries setting:
   1. the retry is set by default to very high number of times (millions) 
   2. _retry.backoff.ms_ defines how often the message will be retried
      1. default → 100 ms
   3. the producer won't try the request forever, it bounded by timeout
      1. delivery.timeout.ms → default 120,000 ms (= 2 minutes)
   
### Safe Producer Configuration
1. _enable.idempotence = true_ (producer level)
2. _min.insync.replicas = 2_ (broker / topic  level)
   
This configuration implies _asks = all_, _retries.MAX_INT_, _max.in.flight.requests.per.connection = 5_

### Message Compression
Producer usually sends data which is text based (such as JSON). In this cas is important to apply compression to the producer:
1. Compression is enabled at the __producer__ level, and it doesn't require any configuration change in the brokers or in the consumers
   1. _compression.type_  → "none" (default), "gzip", "lz4", "snappy"
2. Compression is more effective the bigger the message being sent to kafka.
3. Compressed batch advantages:
   1. much smaller producer request size (compression ratio up to 4x)
   2. faster to transfer data over the network (less latency)
   3. better throughput
   4. better disc utilization in kafka (the messages are stored in the compresed format, which mean they are smaller)
4. Compressed batch disadvantages (very minor):
   1. producers must commit some CPU cycles to compress data
   2. consumers must commit some CPU cycles to decompress data
   


