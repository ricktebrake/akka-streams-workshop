package nl.quintor.akkastreams;

import java.math.BigDecimal;
import lombok.Data;
import lombok.With;
import nl.quintor.akkastreams.StreamingBeer.BeerStyle;

@Data
@With
public class Beer {

  private final String name;
  private final String brewery;
  private final String country;
  private final BeerStyle style;
  private final Double abv;
  private final BigDecimal price;

}
