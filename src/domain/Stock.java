package domain;

import java.math.BigDecimal;

public class Stock
{
  private final String symbol;
  private StockState currentState;
  private BigDecimal currentPrice;

  public Stock(String symbol, StockState currentState, BigDecimal currentPrice)
  {
    this.symbol = symbol.toUpperCase();
    this.currentState = currentState;
    this.currentPrice = currentPrice;
  }

  // Factory method for new Stock
  public static Stock createNew(String symbol, String name, BigDecimal currentPrice) {
    return new Stock(symbol, StockState.STEADY, currentPrice);
  }

  // Factory method for recreating from storage
  public static Stock recreateFromStorage(String symbol, String name, StockState currentState, BigDecimal currentPrice) {
    return new Stock(symbol,  currentState, currentPrice);
  }

  public String getSymbol()
  {
    return symbol;
  }

  public StockState getCurrentState()
  {
    return currentState;
  }

  public BigDecimal getCurrentPrice()
  {
    return currentPrice;
  }
}
