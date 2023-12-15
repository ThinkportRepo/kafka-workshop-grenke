package digital.thinkport;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.kafka.streams.kstream.Grouped.with;

public class StatefulStream {

    public static void main(String[] args) {
        Properties properties =  getProperties();
        AtomicLong atomicLong = new AtomicLong(0);
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //TOPOLOGY


        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();


    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "{bootstrap-server}");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='{username}' password='{password}';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"elf-factory-aggregator");
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        //SCHEMA-REGISTRY#

        properties.put("schema.registry.url", "{schema-reg-url}");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "{schema-reg-user}:{schema-reg-password}");
        return properties;
    }

    private static SpecificAvroSerde<OrderedPresentChecked>  getOrderedPresentCheckedSerde(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<PresentsPerRecipient>  getPresentsPerRecipientSerde(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<PresentRecipient>  getPresentRecipientSerde(Properties properties) {
        return getGenericSerde(properties);
    }

    private static SpecificAvroSerde<Wishlist>  getWishlistSerde(Properties properties) {
        return getGenericSerde(properties);
    }

    private static <T extends SpecificRecord> SpecificAvroSerde<T> getGenericSerde(Properties properties){
        final Map<String, String> genericSerdeConfig = new HashMap<>();
        genericSerdeConfig.put("schema.registry.url", properties.getProperty("schema.registry.url"));
        genericSerdeConfig.put("basic.auth.credentials.source", properties.getProperty("basic.auth.credentials.source"));
        genericSerdeConfig.put("basic.auth.user.info", properties.getProperty("basic.auth.user.info"));
        final SpecificAvroSerde<T> genericSerde = new SpecificAvroSerde<>();
        genericSerde.configure(genericSerdeConfig, false); // `false` for record values
        return genericSerde;
    }

}