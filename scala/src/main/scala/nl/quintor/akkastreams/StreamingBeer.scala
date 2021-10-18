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


      val beerSource = Source(beers).filter(_.abv >= 0.5)

      val calculateImportTax = Flow[Beer].map(beer => beer.country match {
        case "UK" | "US" => beer.copy(price = beer.price * 1.3)
        case _ => beer.copy(price = beer.price * 1.1)
      })
      val bockDiscount = Flow[Beer].map(beer => beer.style match {
        case Bock => beer.copy(price = beer.price * 0.9)
        case _ => beer
      })

      val averagePrice = Flow[Beer]
        .fold((BigDecimal(0), 0))((result, beer) => (result._1 + beer.price, result._2 + 1))
        .map(total => total._1 / total._2)

      val highestAbv = Flow[Beer].fold(0.doubleValue)((highestAbv, beer) => if (beer.abv > highestAbv) beer.abv else highestAbv)

      val generateMenuItem = Flow[Beer].groupBy(10, _.style)
        .map(beer => s"${beer.name} \t| Brouwerij: ${beer.brewery} \t| ABV: ${beer.abv} \t| Prijs: ${beer.price}")
        .reduce((menu, item) => menu + "\n" + item).map(_+ "\n")

      val printToConsole = Sink.foreach(Console.println)

      val bcast1 = builder.add(Broadcast[Beer](2))
      val bcast2 = builder.add(Broadcast[Beer](2))


      beerSource ~> calculateImportTax ~> bockDiscount ~> bcast1 ~> generateMenuItem.mergeSubstreams ~> printToConsole
      bcast1 ~> bcast2 ~> averagePrice ~> printToConsole
      bcast2 ~> highestAbv ~> printToConsole

      ClosedShape
  }).run()
}

case class Beer(name: String, brewery: String, country: String, style: BeerStyle, abv: Double, price: BigDecimal)