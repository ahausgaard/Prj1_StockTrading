package business.services.trading.fees;

import java.math.BigDecimal;

public class PercentageFeeStrategy implements FeeStrategy
{
  @Override public BigDecimal calculateFee(BigDecimal transactionAmount)
  {
    double feeRate = shared.configuration.AppConfig.getInstance().getTransactionFee();
    return transactionAmount.multiply(BigDecimal.valueOf(feeRate));
  }
}
