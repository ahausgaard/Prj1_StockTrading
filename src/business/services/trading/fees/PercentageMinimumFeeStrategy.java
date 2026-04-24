package business.services.trading.fees;

import java.math.BigDecimal;

public class PercentageMinimumFeeStrategy implements FeeStrategy
{
  @Override public BigDecimal calculateFee(BigDecimal transactionAmount)
  {
    double feeRate = shared.configuration.AppConfig.getInstance().getTransactionFee();
    BigDecimal fee = transactionAmount.multiply(BigDecimal.valueOf(feeRate));
    BigDecimal minimumFee = shared.configuration.AppConfig.getInstance().getMinimumTransactionFee();
    return fee.compareTo(minimumFee) < 0 ? minimumFee : fee;
  }
}
