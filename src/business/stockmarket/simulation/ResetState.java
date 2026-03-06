package business.stockmarket.simulation;

import java.math.BigDecimal;

public class ResetState implements LiveStockState
{
  @Override public BigDecimal calculatePriceChange()
  {
    return BigDecimal.ZERO;
  }

  @Override public String getName()
  {
    return this.getClass().getSimpleName();
  }
}
