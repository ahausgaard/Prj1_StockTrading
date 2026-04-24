package business.services.trading.fees;

import java.math.BigDecimal;

public interface FeeStrategy
{
  BigDecimal calculateFee(BigDecimal transactionAmount);
}
