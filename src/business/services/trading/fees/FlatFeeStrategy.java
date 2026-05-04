package business.services.trading.fees;

import java.math.BigDecimal;
import java.util.Objects;

public class FlatFeeStrategy implements FeeStrategy
{
    private final BigDecimal flatFee;

    public FlatFeeStrategy(BigDecimal flatFee)
    {
        this.flatFee = Objects.requireNonNull(flatFee, "flatFee must not be null");
    }

    @Override
    public BigDecimal calculateFee(BigDecimal transactionAmount)
    {
        return flatFee;
    }
}
