package business.stockmarket.simulation;

import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class Livestock
{
  private final String symbol;
  private BigDecimal currentPrice;
  private LiveStockState currentState;

  public Livestock(String symbol)
  {
    this.symbol = symbol;
    this.currentState = new SteadyState();
    this.currentPrice = AppConfig.getInstance().getStartingBalance();
  }

  public void updatePrice()
  {
    BigDecimal priceChange = currentState.calculatePriceChange();

    currentPrice = currentPrice.add(priceChange);
  }

}