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

        KStream<String, OrderedPresentChecked> presentCheckedKStream =
                streamsBuilder.stream("factory.presents.checked.0",
                Consumed.with(Serdes.String(), getOrderedPresentCheckedSerde(properties)))
                .peek((k,v)->{if((atomicLong.getAndIncrement() % 10000)==0){
                    System.out.println(atomicLong.get());
                }} ) ;

        KGroupedStream<String, OrderedPresentChecked> kGroupedStream = presentCheckedKStream
                .groupByKey(Grouped.with(Serdes.String(), getOrderedPresentCheckedSerde(properties)));

        final KTable<String, PresentsPerRecipient> presentsPerRecipientKTable =
                kGroupedStream.aggregate(() -> new PresentsPerRecipient("",new LinkedList<>(),0.0),
                        (key, value, aggregate) -> {
                            if(StringUtils.isEmpty(aggregate.getRecipient())){
                                aggregate.setRecipient(key);
                            }
                            if(aggregate.getCumulated()<50
                            && !aggregate.getPresents().contains(value.getBrand() +";"+ value.getProduct())
                            && aggregate.getCumulated()+value.getPrice() < 50){
                                aggregate.getPresents().add(value.getBrand() +";"+ value.getProduct());
                                aggregate.setCumulated(aggregate.getCumulated() + value.getPrice());
                            }
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), getPresentsPerRecipientSerde(properties)));

        // persist the result in topic
        presentsPerRecipientKTable.toStream().to("factory.presents.cumulatedPerRecipient.0", Produced.with(Serdes.String(),getPresentsPerRecipientSerde(properties)));


        KTable<String, PresentRecipient> recipients = streamsBuilder.table("factory.presents.recipients.0", Consumed.with(Serdes.String(),getPresentRecipientSerde(properties)));
        KTable<String, PresentsPerRecipient> presentsPerRecipient = streamsBuilder.table("factory.presents.cumulatedPerRecipient.0", Consumed.with(Serdes.String(),getPresentsPerRecipientSerde(properties)));
        KTable<String,Wishlist> wishlist = presentsPerRecipient.join(recipients,(presents,recipient)-> new Wishlist(recipient.getName(),recipient.getAge(), recipient.getAddress(),presents.getPresents(),presents.getCumulated()),Materialized.with(Serdes.String(),getWishlistSerde(properties)));
        wishlist.toStream().to("factory.presents.wishlists.0",Produced.with(Serdes.String(),getWishlistSerde(properties)));



        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();


    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        //properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //properties.put("schema.registry.url", "http://localhost:8081");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-75m1o.europe-west3.gcp.confluent.cloud:9092");
        properties.put("security.protocol" , "SASL_SSL");
        properties.put("sasl.jaas.config" , "org.apache.kafka.common.security.plain.PlainLoginModule required username='MTHMXNOJJOMJDWKC' password='E5AL3BwH3tvuz7nnZyc4T/ENN2TC0UUNTOces8gPefP2jtL+G5HRE8hjgI1bpFgJ';");
        properties.put("sasl.mechanism" , "PLAIN");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"elf-factory-aggregator");
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        //SCHEMA-REGISTRY#

        properties.put("schema.registry.url", "https://psrc-2312y.europe-west3.gcp.confluent.cloud");
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", "3SYHEWSXNVO7EG3P:9xJ+x9hW2AHEQIaqzBifBMBcgWP1kINcXNRy7+fGIe6xlCOlI1UzjVjXvtHMUenQ");
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