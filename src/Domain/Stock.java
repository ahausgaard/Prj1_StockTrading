package Domain;

import java.math.BigDecimal;

public class Stock
{
  private final String symbol;

  private String name;
  private StockState currentState;
  private BigDecimal currentPrice;


  public Stock(String symbol, String name, StockState currentState, BigDecimal currentPrice)
  {
    this.symbol = symbol.toUpperCase();
    this.name = name;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getName()
  {
    return name;
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
