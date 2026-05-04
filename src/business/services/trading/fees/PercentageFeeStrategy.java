package business.services.trading.fees;

import java.math.BigDecimal;
import java.util.Objects;

public class PercentageFeeStrategy implements FeeStrategy
{
  private final BigDecimal feeRate;

  public PercentageFeeStrategy(double feeRate)
  {
    this.feeRate = BigDecimal.valueOf(feeRate);
  }

  @Override public BigDecimal calculateFee(BigDecimal transactionAmount)
  {
    Objects.requireNonNull(transactionAmount, "transactionAmount must not be null");
    return transactionAmount.multiply(feeRate);
  }
}
