package business.services.trading.fees;

import java.math.BigDecimal;
import java.util.Objects;

public class PercentageMinimumFeeStrategy implements FeeStrategy
{
  private final BigDecimal feeRate;
  private final BigDecimal minimumFee;

  public PercentageMinimumFeeStrategy(double feeRate, BigDecimal minimumFee)
  {
    this.feeRate = BigDecimal.valueOf(feeRate);
    this.minimumFee = Objects.requireNonNull(minimumFee, "minimumFee must not be null");
  }

  @Override public BigDecimal calculateFee(BigDecimal transactionAmount)
  {
    Objects.requireNonNull(transactionAmount, "transactionAmount must not be null");
    BigDecimal fee = transactionAmount.multiply(feeRate);
    return fee.compareTo(minimumFee) < 0 ? minimumFee : fee;
  }
}
