package nl.quintor.akkastreams

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttMessage, MqttQoS, MqttSubscriptions}
import akka.stream.scaladsl.Source
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.Future

object StreamingMQTT extends App {
  implicit val system: ActorSystem = ActorSystem("Workshop")

  val connectionSettings = MqttConnectionSettings(
    "tcp://test.mosquitto.org:1883", // (1)
    "test-scala-client", // (2)
    new MemoryPersistence // (3)
  )

  val mqttSource: Source[MqttMessage, Future[Done]] =
    MqttSource.atMostOnce(
      connectionSettings,
      MqttSubscriptions("#", MqttQoS.atMostOnce),
      bufferSize = 8
    )
  mqttSource.map(message => (message.topic, message.payload.decodeString("UTF-8")))
    .filter(_._1.contains("/go-eCharger/"))
    .runForeach(Console.println)


}
