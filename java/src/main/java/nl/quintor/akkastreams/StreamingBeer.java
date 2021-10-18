package nl.quintor.akkastreams;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ClosedShape;
import akka.stream.Outlet;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletionStage;
import scala.Tuple2;

public class StreamingBeer {

  public static void main(String[] args) {
    final ActorSystem system = ActorSystem.create("QuickStart");

    var beers = List.of(
        new Beer("Herfstbok", "Grolsch", "NL", BeerStyle.Bock, 6.5, BigDecimal.valueOf(2.50)),
        new Beer("Celebrator Doppelbock", "Ayinger", "DE", BeerStyle.Bock, 6.7, BigDecimal.valueOf(2.60)),
        new Beer("Motorolie", "Moersleutel", "NL", BeerStyle.Stout, 12.0, BigDecimal.valueOf(5.00)),
        new Beer("Chateau Neuborg", "Gulpener", "NL", BeerStyle.Lager, 5.5, BigDecimal.valueOf(2.70)),
        new Beer("Premium Pilsner", "Grolsch", "NL", BeerStyle.Lager, 5.2, BigDecimal.valueOf(2.50)),
        new Beer("Triple Porter", "Pott's", "DE", BeerStyle.Porter, 9.8, BigDecimal.valueOf(3.80)),
        new Beer("Jubiläums Pilsener", "Berliner Kindl", "DE", BeerStyle.Lager, 5.1, BigDecimal.valueOf(3.50)),
        new Beer("Tripel Hop Citra", "Duvel", "BE", BeerStyle.IPA, 9.5, BigDecimal.valueOf(3.30)),
        new Beer("Nanny State", "Brewdog", "UK", BeerStyle.IPA, 0.2, BigDecimal.valueOf(2.40)),
        new Beer("Breakfast Stout", "Founders", "US", BeerStyle.Stout, 4.5, BigDecimal.valueOf(5.60)),
        new Beer("60 Minute IPA", "Dogfish Head", "US", BeerStyle.IPA, 6.0, BigDecimal.valueOf(3.40)),
        new Beer("Out Of The Woods", "Ampersand", "UK", BeerStyle.Bock, 7.5, BigDecimal.valueOf(5.80)),
        new Beer("Premium 1664", "Kronenbourg", "FR", BeerStyle.Lager, 5.5, BigDecimal.valueOf(2.00))
    );

    /**
     * Create a stream to process the list of beers using the GraphDSL
     * The stream should have the following outputs:
     * - A formatted menu of the beers grouped by style
     * - A small report with the average price of all beers and the beer with the highest ABV
     *
     * The following rules apply
     * - All beers from UK and US get a 30% price increase
     * - All beers from other countries get a 10% price increase
     * - All bock beers are discounted with 10%
     * - No alcohol-free beers ( < 0.5% )
     * - A menu item contains name, brewery, abv and price
     */
    var beerSource = Source.from(beers).filter(beer -> beer.getAbv() >= 0.5);

    var calculateImportTax = Flow.of(Beer.class).map(beer -> {
      if (beer.getCountry().equals("UK") || beer.getCountry().equals("US")) {
        return beer.withPrice(beer.getPrice().multiply(BigDecimal.valueOf(1.3)));
      } else {
        return beer.withPrice(beer.getPrice().multiply(BigDecimal.valueOf(1.1)));
      }
    });

    var bockDiscount = Flow.of(Beer.class).map(beer -> {
      if (beer.getStyle().equals(BeerStyle.Bock)) {
        return beer.withPrice(beer.getPrice().multiply(BigDecimal.valueOf(0.9)));
      } else {
        return beer;
      }
    });

    var averagePrice = Flow.of(Beer.class).fold(Tuple2.apply(BigDecimal.ZERO, 0),
            (result, beer) -> Tuple2.apply(result._1.add(beer.getPrice()), result._2 + 1))
        .map(total -> total._1.divide(BigDecimal.valueOf(total._2)));

    var highestAbv = Flow.of(Beer.class)
        .fold((double) 0, (highAbv, beer) -> beer.getAbv() > highAbv ? beer.getAbv() : highAbv);

    var generateMenuItem = Flow.of(Beer.class).groupBy(10, Beer::getStyle)
        .map(beer -> String.format("%s \t| Brouwerij: %s \t| ABV: %s \t| Prijs: %s", beer.getName(),
            beer.getBrewery(), beer.getAbv(), beer.getPrice()))
        .reduce((menu, item) -> menu + "\n" + item).map(menu -> menu + "\n");

    Sink<Object, CompletionStage<Done>> printToConsole = Sink.foreach(System.out::println);

    RunnableGraph.fromGraph(
        GraphDSL.create(builder -> {
          final UniformFanOutShape<Beer, Beer> bcast1 = builder.add(Broadcast.create(2));
          final UniformFanOutShape<Beer, Beer> bcast2 = builder.add(Broadcast.create(2));

          Outlet<Beer> source = builder.add(beerSource).out();
          builder.from(source)
              .via(builder.add(calculateImportTax))
              .via(builder.add(bockDiscount))
              .viaFanOut(bcast1)
              .via(builder.add(generateMenuItem.mergeSubstreams()))
              .to(builder.add(printToConsole));
          builder.from(bcast1)
              .viaFanOut(bcast2)
              .via(builder.add(averagePrice))
              .to(builder.add(printToConsole));
          builder.from(bcast2)
              .via(builder.add(highestAbv))
              .to(builder.add(printToConsole));

          return ClosedShape.getInstance();
        })).run(system);
  }

  enum BeerStyle {Bock, Stout, Lager, Porter, IPA;}
}
