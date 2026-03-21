package business.dto;

import domain.StockState;

import java.math.BigDecimal;

public record StockDTO(String symbol, BigDecimal currentPrice, StockState state)
{
}

