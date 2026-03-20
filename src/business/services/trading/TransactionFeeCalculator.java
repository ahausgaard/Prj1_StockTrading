package business.services.trading;

import java.math.BigDecimal;

public class TransactionFeeCalculator {
    
    public static BigDecimal calculateFee(BigDecimal totalAmount) {
        double feeRate = shared.configuration.AppConfig.getInstance().getTransactionFee();
        BigDecimal fee = totalAmount.multiply(BigDecimal.valueOf(feeRate));
        BigDecimal minimumFee = shared.configuration.AppConfig.getInstance().getMinimumTransactionFee();
        return fee.compareTo(minimumFee) < 0 ? minimumFee : fee;
    }
}

