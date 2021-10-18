package nl.quintor.akkastreams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.{Done, NotUsed}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object StreamingNumbers extends App {
  implicit val system: ActorSystem = ActorSystem("Workshop")

  val numberSource: Source[Int, NotUsed] = Source(1 to 20)
  val higherNumbers: Source[Int, NotUsed] = Source(20 to 40)

  //Print all numbers  to console

  //Print all even numbers

  //Print the sum of all even numbers and the sum of all uneven numbers

  //Print pairs of numbers from both sources

  //Print one number per second

}
