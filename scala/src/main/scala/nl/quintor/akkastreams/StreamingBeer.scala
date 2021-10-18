package nl.quintor.akkastreams

object BeerStyle extends Enumeration {
  type BeerStyle = Value
  val Lager, Bock, IPA, Porter, Stout = Value
}

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.GraphDSL.Implicits.{SourceArrow, fanOut2flow}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import nl.quintor.akkastreams.BeerStyle._

object StreamingBeer extends App {
  implicit val system: ActorSystem = ActorSystem("Workshop")

  val beers = Seq(
    Beer("Herfstbok", "Grolsch", "NL", BeerStyle.Bock, 6.5, 2.50),
    Beer("Celebrator Doppelbock", "Ayinger", "DE", BeerStyle.Bock, 6.7, 2.60),
    Beer("Motorolie", "Moersleutel", "NL", BeerStyle.Stout, 12.0, 5.00),
    Beer("Chateau Neuborg", "Gulpener", "NL", BeerStyle.Lager, 5.5, 2.70),
    Beer("Premium Pilsner", "Grolsch", "NL", BeerStyle.Lager, 5.2, 2.50),
    Beer("Triple Porter", "Pott's", "DE", BeerStyle.Porter, 9.8, 3.80),
    Beer("Jubiläums Pilsener", "Berliner Kindl", "DE", BeerStyle.Lager, 5.1, 3.50),
    Beer("Tripel Hop Citra", "Duvel", "BE", BeerStyle.IPA, 9.5, 3.30),
    Beer("Nanny State", "Brewdog", "UK", BeerStyle.IPA, 0.2, 2.40),
    Beer("Breakfast Stout", "Founders", "US", BeerStyle.Stout, 4.5, 5.60),
    Beer("60 Minute IPA", "Dogfish Head", "US", BeerStyle.IPA, 6.0, 3.40),
    Beer("Out Of The Woods", "Ampersand", "UK", BeerStyle.Bock, 7.5, 5.80),
    Beer("Premium 1664", "Kronenbourg", "FR", BeerStyle.Lager, 5.5, 2.00)
  )

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
  RunnableGraph.fromGraph(GraphDSL.create() {
    implicit builder =>


      ClosedShape
  }).run()
}

case class Beer(name: String, brewery: String, country: String, style: BeerStyle, abv: Double, price: BigDecimal)