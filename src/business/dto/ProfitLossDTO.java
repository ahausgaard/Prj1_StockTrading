package business.dto;

import java.math.BigDecimal;

public record ProfitLossDTO(
    BigDecimal totalBuyCost,
    BigDecimal totalSellRevenue,
    BigDecimal totalFeesSpent,
    BigDecimal netProfitLoss)
{
}

