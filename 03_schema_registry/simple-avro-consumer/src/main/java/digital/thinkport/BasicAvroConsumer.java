package digital.thinkport;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class BasicAvroConsumer {
    public static void main(String[] args) throws InterruptedException {
        Properties properties = getProperties();
        HashMap<String,OrderedPresent> lastOrders = new HashMap<>();

        KafkaConsumer<String, OrderedPresent> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(List.of("factory.presents.ordered.0"));
        //consumer.subscribe(List.of("factory.presents.recipients.0"));
        int i = 0;
        while(true) {


            ConsumerRecords<String, OrderedPresent> results = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            for (ConsumerRecord<String, OrderedPresent> result : results) {
                if((i % 10000) == 0){
                    System.out.println(i);
                }
                lastOrders.put(result.key(), result.value());
                //System.out.printf("Message received in topic %s and partition %s, key was %s, value was %s %n", result.topic(), result.partition(), result.key(), result.value().getPrice());
                //sleep(1000);
                i ++;
            }
            /*
            result.
            result.forEach((r)-> {

            );

             */
            consumer.commitSync();
        }
    }
    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put("schema.registry.url", "http://localhost:8081");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        //properties.put("security.protocol" , "SASL_SSL");
        //properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='MTHMXNOJJOMJDWKC' password='E5AL3BwH3tvuz7nnZyc4T/ENN2TC0UUNTOces8gPefP2jtL+G5HRE8hjgI1bpFgJ';");
        //properties.put("sasl.mechanism" , "PLAIN");
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group2");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        //SCHEMA-REGISTRY
        //properties.put("schema.registry.url", "https://psrc-2312y.europe-west3.gcp.confluent.cloud");
        //properties.put("basic.auth.credentials.source", "USER_INFO");
        //properties.put("basic.auth.user.info", "3SYHEWSXNVO7EG3P:9xJ+x9hW2AHEQIaqzBifBMBcgWP1kINcXNRy7+fGIe6xlCOlI1UzjVjXvtHMUenQ");
        return properties;
    }




}
