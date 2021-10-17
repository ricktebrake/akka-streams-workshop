package nl.ricktebrake.akkastreamsworkshop;

import akka.stream.*;
import akka.stream.javadsl.*;
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.util.ByteString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class App {

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("QuickStart");

    final Source<Integer, NotUsed> source = Source.range(1, 100);
    final Source<Integer, NotUsed> source2 = Source.range(300,400);
    Source.combine(source, source2, new ArrayList<>(), Merge::create).groupBy(50, i-> i % 5).reduce(Integer::sum).async().mergeSubstreams()
    .runForeach(System.out::println, system);


//
//    source.runForeach(System.out::println, system);
//
//    final Sink<Integer, CompletionStage<Integer>> sink =
//        Sink.fold(0, Integer::sum);
//
//    CompletionStage<Integer> completionStage = source.runWith(sink, system);
//    completionStage.whenComplete((integer, throwable) -> System.out.println(integer));

  }
}