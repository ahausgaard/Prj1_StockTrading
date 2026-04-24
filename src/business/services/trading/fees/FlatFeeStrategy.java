package business.services.trading.fees;

import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class FlatFeeStrategy implements FeeStrategy
{
    @Override
    public BigDecimal calculateFee(BigDecimal transactionAmount)
    {
        return AppConfig.getInstance().getMinimumTransactionFee();
    }
}
