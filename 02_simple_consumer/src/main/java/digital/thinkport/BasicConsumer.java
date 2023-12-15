package digital.thinkport;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BasicConsumer {
    public static void main(String[] args) {
        Properties properties = getProperties();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of("factory.presents.ordered.0"));

        while(true) {
            ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            result.forEach(System.out::println);
            consumer.commitSync();
        }
    }

    private static void consumeMessagesSyncCommit(KafkaConsumer<String,String> consumer){
        ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        result.forEach(System.out::println);
        consumer.commitSync();
    }

    private static void consumeMessagesSpecificOffsetCommit(KafkaConsumer<String,String> consumer){
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = new HashMap<>();
        ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        for (ConsumerRecord<String, String> consumerRecord : result) {
            System.out.println(consumerRecord);
            offsetAndMetadataMap.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                    new OffsetAndMetadata(consumerRecord.offset()));
            consumer.commitSync(offsetAndMetadataMap);
        }
    }
    private static void consumeMessagesAsyncCallback(KafkaConsumer<String,String> consumer){
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = new HashMap<>();
        ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        for (ConsumerRecord<String, String> consumerRecord : result) {
            System.out.println(consumerRecord);
            offsetAndMetadataMap.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                    new OffsetAndMetadata(consumerRecord.offset()));
            consumer.commitAsync(offsetAndMetadataMap,
                    (offsets, exception) -> System.out.printf("Callback, offset: %s, exception %s%n", offsets, exception)
                    );
        }
    }

    private static void consumeMessagesAsyncAndSync(KafkaConsumer<String,String> consumer){

            Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = new HashMap<>();
            ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, String> consumerRecord : result) {
                offsetAndMetadataMap.put(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                        new OffsetAndMetadata(consumerRecord.offset()));
                consumer.commitSync(offsetAndMetadataMap);
                System.out.println(consumerRecord);
            }

    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='MTHMXNOJJOMJDWKC' password='E5AL3BwH3tvuz7nnZyc4T/ENN2TC0UUNTOces8gPefP2jtL+G5HRE8hjgI1bpFgJ';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return properties;
    }




}
