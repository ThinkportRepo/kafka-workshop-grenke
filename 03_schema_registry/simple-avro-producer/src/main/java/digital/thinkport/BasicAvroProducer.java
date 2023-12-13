package digital.thinkport;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicAvroProducer {

    public static void main(String[] args) {
        Properties properties =  getProperties();
        KafkaProducer<String, PresentRecipient> producer;
    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "{bootstrap-server}");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='{username}' password='{password}';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        //SCHEMA-REGISTRY
        properties.put("schema.registry.url", "{schema-registry-url}");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "{schema-registry-username}:{schema-registry-password}");
        return properties;
    }


}
