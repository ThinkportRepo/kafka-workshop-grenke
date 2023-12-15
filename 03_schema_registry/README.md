# SCHEMA REGISTRY EXERCISE
## Configure the properties
1. Replace {bootstrap-server} and {username} and {password} and {schema-registry-url} and {schema-registry-username} and {schema-registry-password} in the getProperties Method in the Main class with values given by the trainer
## Build Project
1. try to build the projects using maven. No errors should occur.
   ```sh 
   cd ./simple-avro-consumer
   mvn clean install
   cd ..
   cd ./simple-avro-producer
   mvn clean install
   ```
## Run Project
1. Run the BasicAvroConsumer App
   ```sh 
   java -jar ./simple-avro-consumer/target/grenke-workshop-basic-consumer.jar
   ```
2. Run the BasicAvroProducer App
   ```sh 
   java -jar ./simple-avro-producer/target/grenke-workshop-basic-consumer.jar
   ```

## Generate Sources
1. Inspect the Avro schema of the Producer inside the resources folder. What does it represent ?
2. Run the codegenerator
   ```sh
   cd ./simple-avro-producer
   mvn clean generate-sources
   ```
3. Inspect the generated class.

## Consume Avro Messages
1. Consume the messages from factory.presents.recipients.0 !!! The topic uses the given schema.
2. Print the message content for every message.
3. Store the name of every receipient in a Java Hashset and print the size of this set every 10th message

## Produce Avro Messages
1. Produce a message to factory.presents.recipients.0 !!! The topic uses the given schema.  
   The Message produced should contain a fake name, age and address.
2. Verify with the consumer that the message was produces successfully
3. Have a look at the confluent cloud console, what the message looks like
4. implement a message header using
   ```java 
      ...
      List <Header> headers = new ArrayList<>();
      headers.add(new RecordHeader("user", "{surename}".getBytes()));
      ...
      new ProducerRecord<>(topic,partition,key,value,headers);
      ...
   ```

## Bonus: work with data faker
You might want to generate fake data in Java.  
A recommended library is the net.datafaker datafaker.
Play around with it.
```java 
import net.datafaker.Faker;
...
Faker faker = new Faker();
....
```