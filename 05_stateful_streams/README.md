# STATEFUL STREAM EXERCISE
## Configure the properties
1. Replace {bootstrap-server} and {username} and {password} and {schema-registry-url} and {schema-registry-username} and {schema-registry-password} in the getProperties Method in the Main class with values given by the trainer
## Build Project
1. try to build the project using maven. No errors should occur.
```sh
mvn clean install
```
## Run Project
1. Run the StatefulStream App
```sh 
java -jar target/grenke-workshop-stateful-stream.jar
```
## Stream the messages
1. Stream the messages from factory.presents.checked.0 using the getOrderedPresentCheckedSerde
2. for each message: print out the value. (terminal operation)
    ```java
    .foreach()
    ```   

## Group the stream
Before we can aggregate the messages we need to define on which attribute we can group the stream by.
1. replace the for each with peek
2. Group the messages by key
    ```java
    KGroupedStream<String, OrderedPresentChecked> kGroupedStream = presentCheckedKStream
    .groupByKey(Grouped.with(Serdes.String(), getOrderedPresentCheckedSerde(properties)));
    ```
3. for each message: print out the value. (terminal operation)

## Aggregate the stream
Aggregation needs to take place as follows:  
Every person should receive at maximum 50$ of items value.  
Fill up the present list with "{brand name}+{product name}" until a value of ~50 is reached.  
Messages that overdraw the 50$ mark will be ignored.  

1. replace the for each with peek
2. Group the messages by key
    ```java
   final KTable<String, PresentsPerRecipient> presentsPerRecipientKTable =
        kGroupedStream.aggregate(
                () -> new PresentsPerRecipient("",new LinkedList<>(),0.0),
                (key, value, aggregate) -> {
                    ...
                    return aggregate;
                },
                Materialized.with(Serdes.String(), getPresentsPerRecipientSerde(properties))
        );
    ```
3. for each message: print out the value. (terminal operation)
4. implement the logic for the aggregate. (try it on your own first and check results with for each)
   ```java
   if(aggregate.getCumulated()<50
   && aggregate.getCumulated()+value.getPrice() < 50){
        aggregate.getPresents().add(value.getBrand() +";"+ value.getProduct());
        aggregate.setCumulated(aggregate.getCumulated() + value.getPrice());
   }
    ```

## Persist the results as stream in topic
1. replace the for each with peek
2. produce the messages to factory.presents.cumulatedPerRecipient.0
   ```java
   presentsPerRecipientKTable.toStream().to("{topic name}", Produced.with(Serdes.String(),getPresentsPerRecipientSerde(properties)));
    ```

## Create a second topology for creating the wishlist 
(code can be placed directly below your other implementation, a second topology will be handled by the same topology builder)  
Now that we know the raw lists, we want to enrich the data with the master data of the persons making the wishes.  
With this we can generate a wishlist for santa so that he knows where to deliver what.
1. Create a recipients Ktable from the factory.presents.recipients.0 topic
   ```java
   KTable<String, PresentRecipient> recipients = streamsBuilder.table("{topic}", Consumed.with(Serdes.String(),getPresentRecipientSerde(properties)));
    ```
2. Create a presentsPerRecipient Ktable from the factory.presents.cumulatedPerRecipient.0 topic
   ```java
   KTable<String, PresentsPerRecipient> presentsPerRecipient = streamsBuilder.table("factory.presents.cumulatedPerRecipient.0", Consumed.with(Serdes.String(),getPresentsPerRecipientSerde(properties)));
    ```
3. Are these tables co-partitioned, so that we can join directly ?
4. Join the tables to a KStream of Wishlists 
   ```java
   KTable<String,Wishlist> wishlist = 
   presentsPerRecipient.join(
   recipients,
   (presents,recipient)-> new Wishlist(...),
   Materialized.with(Serdes.String(),getWishlistSerde(properties))
   );
    ```
5. for each message: print out the value. (terminal operation)
6. after trying out (you might want to comment out all the aggregation part) you can create an output stream to the factory.presents.wishlists.0 topic
