package business.stockmarket.simulation;

import business.stockmarket.StockStateConverter;
import domain.Stock;
import domain.StockState;
import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class LiveStock
{
  private final String symbol;
  private BigDecimal currentPrice;
  private LiveStockState currentState;

  public LiveStock(String symbol, LiveStockState currentState, BigDecimal currentPrice)
  {
    this.symbol = symbol;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
  }

  //Factories
  public static LiveStock createNew(String symbol)
  {
    LiveStockState currentState = new SteadyState();
    BigDecimal currentPrice = AppConfig.getInstance().getStartingBalance();

    return new LiveStock(symbol, currentState, currentPrice);
  }

  public static LiveStock fromExisting(Stock existingStock)
  {
    String symbol = existingStock.getSymbol().toUpperCase();
    LiveStockState liveStockState = StockStateConverter.toLiveState(existingStock.getCurrentState());
    BigDecimal currentPrice = existingStock.getCurrentPrice();

    return new LiveStock(symbol, liveStockState, currentPrice);
  }

  public void updatePrice()
  {
    BigDecimal priceChange = currentState.calculatePriceChange();

    currentPrice = currentPrice.add(priceChange);
  }
}