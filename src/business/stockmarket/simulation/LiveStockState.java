package business.stockmarket.simulation;

import java.math.BigDecimal;

public interface LiveStockState
{
  BigDecimal calculatePriceChange();
  String getName();
}
