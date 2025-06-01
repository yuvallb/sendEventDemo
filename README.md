# Send events Demo - PHP

To run a demo:

```bash
cd php
php App.php
```

## Structure

* `App`: Entry point. Demonstrating usage by generating and sending 20 events.
* `EventHandler`: Main event handler.
  * Gets a sender and serializer using dependency injection from the constructor.
  * Contains a private queue for storing events before sending.
  * Synchronously flushes the queue if number of pending events reaches some preset threashold, configured using  `HighWatermark`.
  * Synchronously flushes the queue if time in seconds since last send is larger than preset threashold, configured using  `MaxSecondsToWait`.
  * Messages are sent in bulk, a bulk limit should be set using `MaxMessageSize`.
* `IEvent`: Empty interface to annotate that a class can be sent as an event.
* `NewUserEvent`: An example implementation event class with private, protected and public fields.
* `serialize/IEventSerializer`: An interface for the ability to serialize event object according to the agreed json notation.
* `serialize/FlatEventSerializer`: Example implementation for serializing events using reflection.
* `send/IMessageSender`: An interface for the ability to send serialized messages using HTTP or any other mechanism.
* `send/ConsoleMessageSender`: An example implementation that only writes the messages to the console.

# Send events Demo - Java

Implemented using Java 8. To run a demo:

```bash
cd java
mvn compile
mvn exec:java
```

## Structure

* `com.github.yuvallb.sendEventDemo.App`: Entry point. Demonstrating usage by generating and sending 20 events.
* `com.github.yuvallb.sendEventDemo.EventHandler<T extends IEvent>`: Main event handler.
  * Gets a sender and serializer using dependency injection from the constructor.
  * Contains a private queue for storing events before sending.
  * Starts a new background thread that asynchronously flushes the queue every given period, configured using `BackgroundFlushSeconds`.
  * Synchronously flushes the queue if number of pending events reaches some preset threashold, configured using  `HighWatermark`.
  * Messages are sent in bulk, a bulk limit should be set using `MaxMessageSize`.
* `com.github.yuvallb.sendEventDemo.IEvent`: Empty interface to annotate that a class can be sent as an event.
* `com.github.yuvallb.sendEventDemo.NewUserEvent`: An example implementation event class with private, protected and public fields.
* `com.github.yuvallb.sendEventDemo.serialize.IEventSerializer`: An interface for the ability to serialize event object according to the agreed json notation.
* `com.github.yuvallb.sendEventDemo.serialize.GsonEventSerializer`: Example implementation for serializing events using the Gson library.
* `com.github.yuvallb.sendEventDemo.send.IMessageSender`: An interface for the ability to send serialized messages using HTTP or any other mechanism.
* `com.github.yuvallb.sendEventDemo.send.ConsoleMessageSender`: An example implementation that only writes the messages to the console.
