package digital.thinkport;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StatelessStream {

    public static void main(String[] args) {
        Properties properties =  getProperties();

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //impl. TOPOLOGY



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
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"elf-factory");
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        //SCHEMA-REGISTRY#

        properties.put("schema.registry.url", "{schema-reg-url}");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "{schema-reg-user}:{schema-reg-password}");
        return properties;
    }
    private static SpecificAvroSerde<OrderedPresent>  getOrderedPresentSerde(Properties properties) {
        final Map<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put("schema.registry.url", properties.getProperty("schema.registry.url"));
        serdeConfig.put("basic.auth.credentials.source", properties.getProperty("basic.auth.credentials.source"));
        serdeConfig.put("basic.auth.user.info", properties.getProperty("basic.auth.user.info"));
        final SpecificAvroSerde<OrderedPresent> serde = new SpecificAvroSerde<>();
        serde.configure(serdeConfig, false); // `false` for record values
        return serde;
    }


}