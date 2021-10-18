package nl.quintor.akkastreams;

import akka.actor.ActorSystem;
import akka.stream.ClosedShape;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import java.math.BigDecimal;
import java.util.List;

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
    RunnableGraph.fromGraph(
        GraphDSL.create(builder -> {
          return ClosedShape.getInstance();
        })).run(system);
  }

  enum BeerStyle {Bock, Stout, Lager, Porter, IPA;}
}
