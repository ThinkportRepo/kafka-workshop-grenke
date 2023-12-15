# STATELESS STREAMS EXERCISE
## Configure the properties
1. Replace {bootstrap-server} and {username} and {password} and {schema-registry-url} and {schema-registry-username} and {schema-registry-password} in the getProperties Method in the Main class with values given by the trainer
## Build Project
1. try to build the project using maven. No errors should occur.
```sh
mvn clean install
```
## Run Project
1. Run the StatelessStream App
```sh 
java -jar target/grenke-workshop-stateless-stream.jar
```
## Stream the messages
1. Stream the messages from factory.presents.ordered.0
    ```java
    streamsBuilder.stream("factory.presents.ordered.0", Consumed.with(Serdes.String(), getOrderedPresentSerde(properties)));
    ```
2. for each message: print out the value. (terminal operation)
    ```java
    .foreach((key,value)->System.out.pritnln(v));
    ```   

## Filter the messages
A lot of presents seem to be too expensive !
1. Instead of for each peek in all messages
   ```java
   .peek((k,v)-> System.out.println())
    ```
2. filter all orders with a price greater than 50
    ```java
    .filter((k,v)->...)
    ```   
3. for each message: print out the value. (terminal operation)

## Validate that you checked the orders for compliance
All filtered messages should be trackable inside the elf factory. Mapping the original message in a OrderedPresentChecked format is required.
1. Create a new serde config for the OrderedPresentChecked
2. Replace the for each of the filter step with peek
3. map the filtered stream of OrderedPresent to a stream of new OrderedPresentChecked
    ```java
    .mapValues((v)->{...})
    ```   
4. for each message: print out the value. (terminal operation)

## Produce your result
1. Instead of for each: Stream to topic "factory.presents.checked.0"
    ```java
    .to("{topic}", Consumed.with(Serdes.String(), getOrderedPresentCheckedSerde(properties)));
    ```   