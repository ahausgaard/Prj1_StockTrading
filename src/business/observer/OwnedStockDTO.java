package business.observer;

import domain.StockState;

import java.math.BigDecimal;

public record OwnedStockDTO(String symbol, double quantity, BigDecimal currentPrice, BigDecimal holdingValue, StockState state)
{
}

