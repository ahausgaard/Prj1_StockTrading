package business.stockmarket.simulation;

import java.math.BigDecimal;
import java.util.Random;

public class GrowingState implements LiveStockState
{
  private static final Random random = new Random();

  @Override public BigDecimal calculatePriceChange()
  {
    double percentage;

    if (random.nextDouble() <= 0.2)
    {
      //Between 4% and 9%
      percentage = (random.nextDouble() * 5 + 4);
    }
    else {
      //Between 1% and 3%
      percentage = (random.nextDouble() * 2 + 1);
    }

    return BigDecimal.valueOf(percentage / 100.0);
  }


  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
