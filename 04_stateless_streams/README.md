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
A lot of prsents seem to be too expensive !
1. filter all messages with a price greater than 50
    ```java
    .filter((k,v)->...)
    ```   
2. for each message: print out the value. (terminal operation)

## Validate that you checked the orders for compliance
All filtered messages should be in a OrderedPresentChecked format
1. Create a new serde config for the OrderedPresentChecked
2. map the filtered stream of OrderedPresent to a stream of new OrderedPresentChecked
    ```java
    .mapValues((v)->{...})
    ```   
3. for each message: print out the value. (terminal operation)

## Produce your result
1. Stream to topic "factory.presents.checked.0"
   .to("{topic}", Consumed.with(Serdes.String(), getOrderedPresentCheckedSerde(properties)));
