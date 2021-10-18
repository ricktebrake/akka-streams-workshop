package nl.quintor.akkastreams;

import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import java.time.Duration;

public class StreamingNumbers {

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("QuickStart");

    var numberSource = Source.range(1, 20);
    var higherNumbers = Source.range(20, 40);

    //Print all numbers  to console
    var printAllNumbers = numberSource.runForeach(System.out::println, system);

    //Print all even numbers
    var printEvenNumbers = numberSource.filter(number -> number % 2 == 0).runForeach(System.out::println, system);

    //Print the sum of all even numbers and the sum of all uneven numbers
    var splitSource = numberSource
        .groupBy(2, number -> number % 2)
        .reduce(Integer::sum).mergeSubstreams()
        .runForeach(System.out::println, system);

    //Print pairs of numbers from both sources
    var combineSource = numberSource
        .zipWith(higherNumbers, (i, j) -> String.format("%d - %d", i, j))
        .runForeach(System.out::println, system);

    //Print one number per second
    var printOneNumberPerSecond = numberSource.throttle(1, Duration.ofSeconds(1)).runForeach(System.out::println,
        system);

  }
}