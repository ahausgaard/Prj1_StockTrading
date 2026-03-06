package business.stockmarket.simulation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public class SteadyState implements LiveStockState
{
  private static final Random random = new Random();

  @Override public BigDecimal calculatePriceChange()
  {
    //Returns random number between -2% and +2%
    double percentage = (random.nextDouble() * 4.0) - 2.0;
    return BigDecimal.valueOf(percentage / 100.0);
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
