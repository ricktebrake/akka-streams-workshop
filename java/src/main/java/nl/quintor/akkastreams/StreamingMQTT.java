package nl.quintor.akkastreams;

import akka.actor.ActorSystem;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.MqttSubscriptions;
import akka.stream.alpakka.mqtt.javadsl.MqttSource;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import scala.Tuple2;

public class StreamingMQTT {

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("QuickStart");

    MqttConnectionSettings connectionSettings = MqttConnectionSettings.create(
        "tcp://test.mosquitto.org:1883", // (1)
        "test-scala-client", // (2)
        new MemoryPersistence() // (3)
    );

    var mqttSource =
        MqttSource.atMostOnce(
            connectionSettings,
            MqttSubscriptions.create("#", MqttQoS.atMostOnce()),
            8
        );

    mqttSource.map(message -> new Tuple2<>(message.topic(), message.payload().decodeString("UTF-8")))
        .runForeach(tuple -> System.out.println(tuple._2), system);
  }

}
