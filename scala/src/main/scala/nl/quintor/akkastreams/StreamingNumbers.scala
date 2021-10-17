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

  val printAllNumbers: Future[Done] = numberSource.runForeach(println(_))

  val printEvenNumbers = numberSource.filter(_ % 2 == 0).runForeach(println(_))
  val splitSource = numberSource
    .groupBy(2, _ % 2)
    .reduce(_ + _).mergeSubstreams
    .runForeach(println(_))

  val combineSource = numberSource
    .zipWith(higherNumbers)((i, j) => s"$i - $j")
    .runForeach(println(_))

  val printOneNumberPerSecond = numberSource.throttle(1, 1.second).runForeach(println(_))
}