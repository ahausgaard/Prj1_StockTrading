package business.services.trading.fees;

import java.math.BigDecimal;

public class FlatFeeStrategy implements FeeStrategy
{
  @Override public BigDecimal calculateFee(BigDecimal transactionAmount)
  {
    return null;
  }
}
