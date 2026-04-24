package business.services.trading.fees;

import java.math.BigDecimal;

public class FlatFeeStrategy implements FeeStrategy
{
    private static final BigDecimal FLAT_FEE = new BigDecimal("15.00");

    @Override
    public BigDecimal calculateFee(BigDecimal transactionAmount)
    {
        return FLAT_FEE;
    }
}
