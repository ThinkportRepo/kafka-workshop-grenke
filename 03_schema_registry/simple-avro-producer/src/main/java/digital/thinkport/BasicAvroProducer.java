package digital.thinkport;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import net.datafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicAvroProducer {

    public static void main(String[] args) {
        Properties properties =  getProperties();
        KafkaProducer<String, PresentRecipient> producer = new KafkaProducer<>(properties);

        Faker faker = new Faker();
        String name = faker.rickAndMorty().character();
        int age = faker.random().nextInt(1,100);
        String address = faker.address().fullAddress();

        PresentRecipient presentRecipient = new PresentRecipient(name,age,address);
        producer.send(new ProducerRecord<>("factory.presents.recipients.0",name,presentRecipient));
        producer.flush();
        producer.close();
    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='MTHMXNOJJOMJDWKC' password='E5AL3BwH3tvuz7nnZyc4T/ENN2TC0UUNTOces8gPefP2jtL+G5HRE8hjgI1bpFgJ';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        //SCHEMA-REGISTRY
        properties.put("schema.registry.url", "https://psrc-2312y.europe-west3.gcp.confluent.cloud");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "3SYHEWSXNVO7EG3P:9xJ+x9hW2AHEQIaqzBifBMBcgWP1kINcXNRy7+fGIe6xlCOlI1UzjVjXvtHMUenQ");
        return properties;
    }


}
