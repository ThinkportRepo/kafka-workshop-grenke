# BASIC CONSUMER EXERCISE
## Configure the properties
1. Replace {bootstrap-server} and {username} and {password} in the getProperties Method in the Main class with values given by the trainer
## Build Project
1. try to build the project using maven. No errors should occur. 
```sh 
cd 02_simple_consumer
mvn clean install
```
## Run Project
1. Run the BasicConsumer App
```sh 
java -jar target/grenke-workshop-basic-consumer.jar
```
## Consume Messages
1. Consume the messages from factory.presents.ordered.0 and print the message content
2. If no messages show up, think about the concept of consumer groups    private static void consumeMessagesSyncCommit(KafkaConsumer<String,String> consumer){
   ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
   result.forEach(System.out::println);
   consumer.commitSync();
   }

## Implement a Synchronous Commit of the offset for the whole consumed batch
1. Set the Properties accordingly so that manual commits are enabled
2. Use following snippet to achieve a Synchronous Commit of the offset for the whole consumed batch
    ```java
    private static void consumeMessagesSyncCommit(KafkaConsumer<String,String> consumer){
        ConsumerRecords<String, String> result = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        result.forEach(System.out::println);
        consumer.commitSync();
    }
    ```

## Implement a Synchronous Commit for every message in the consumed batch
1. think about the reason why a batch commit might not be the best solution for every scenario
2. Use following snippet to achieve a Synchronous Commit for every message in the consumed batch
    ```java
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
    ```
## Implement a Asynchronous Commit for every message in the consumed batch
1. sync commits are blocking the thread until the broker responds. Why would a async call make sense ?
2. Use following snippet to achieve a Asynchronous Commit for every message in the consumed batch
    ```java
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
    ```
3. Bonus: Async Commits do not retry if a retryable exception happens. Sync commits do so. Why ?

## An application is required to print out every message as efficient as possible. A data loss is rarely accepted but no duplicates may occur.
1. Write a consumer that is achieving this behaviour.