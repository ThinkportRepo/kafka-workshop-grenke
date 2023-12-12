# BASIC PRODUCER EXERCISE
## Configure the properties
1. Replace {bootstrap-server} and {username} and {password} in the getProperties Method in the Main class with values given by the trainer
## Build Project
1. try to build the project using maven. No errors should occur. 
```sh 
cd 01_simple_producer
mvn clean install
```
## Run Project
1. Run the BasicProducer App
```sh 
java -jar target/grenke-workshop-basic-producer.jar
```
## Create the topics
1. Rename {surename} in the Main Class with your surename (leave out {})
2. crate the {surename}-my-first-topic in the Confluent Cloud with three partitions and a retention period of 1 day
## Produce Messages
1. Produce one Message to the {surename}-my-first-topic
2. Verify that the message was produced in Confluent Cloud
3. Produce five more messages to the {surename}-my-first-topic
4. Produce one more message to the {surename}-my-first-topic using any key
5. Produce five more messages to the {surename}-my-first-topic Partition 2
6. Implement a call back handler to the producer to print out the offset the message is sent to and test it.
    ```java
    import org.apache.kafka.clients.producer.KafkaProducer;
    
    public static void sendWithCallback(KafkaProducer producer, ProducerRecord producerRecord) {
        producer.send(producerRecord, (recordMetadata, exception) -> {
            if (exception == null) {
                System.out.println("Record written to offset " +
                        recordMetadata.offset() + " timestamp " +
                        recordMetadata.timestamp());
            } else {
                System.err.println("An error occurred");
                exception.printStackTrace(System.err);
            }
        });
    }
    ```
7. Add a custom header to the messages you send out consisting of the key-value pair "producer-name" and "{surename}"
   (this is the bonus exercise, so there are no hints)