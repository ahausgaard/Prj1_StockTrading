package business.stockmarket.simulation;

import java.math.BigDecimal;
import java.util.Random;

public class DecliningState implements LiveStockState
{
  private final Random random = new Random();

  @Override public BigDecimal calculatePriceChange()
  {
    double percentage;

    if (random.nextDouble() <= 0.2)
    {
      //Between -3% and -8%
      percentage = -(random.nextDouble() * 5 + 3);
    }
    else {
      //Between -1% and -3%
      percentage = -(random.nextDouble() * 2 + 1);
    }

    return BigDecimal.valueOf(percentage / 100.0);
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
