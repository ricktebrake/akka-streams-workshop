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

    //Print all even numbers

    //Print the sum of all even numbers and the sum of all uneven numbers

    //Print pairs of numbers from both sources

    //Print one number per second

  }
}