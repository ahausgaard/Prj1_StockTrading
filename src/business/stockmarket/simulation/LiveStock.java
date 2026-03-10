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
  private final TransitionManager transitionManager;
  private long bankruptSince = -1;

  public LiveStock(String symbol, LiveStockState currentState, BigDecimal currentPrice)
  {
    this.symbol = symbol;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
    this.transitionManager = new TransitionManager();
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

  public Stock toStock()
  {
    return new Stock(symbol, StockStateConverter.toDomainState(currentState), currentPrice);
  }

  public void updatePrice()
  {
    BigDecimal priceChange = currentPrice.multiply(currentState.calculatePriceChange());
    currentPrice = currentPrice.add(priceChange);

    // State transition
    StockState currentStockState = StockStateConverter.toDomainState(currentState);
    //Bankrupt penalty logic
    if (currentStockState == StockState.BANKRUPT)
    {
      if (bankruptSince < 0)
      {
        bankruptSince = System.currentTimeMillis();
      }
      if (System.currentTimeMillis() - bankruptSince < AppConfig.getInstance()
          .getBankruptcyPenaltyMs())
      {
        return;
      }
      bankruptSince = -1; // penalty served, allow transition
      currentPrice = AppConfig.getInstance().getStartingBalance();
    }
    StockState nextStockState = transitionManager.getNextState(currentStockState);
    LiveStockState nextLiveStockState = StockStateConverter.toLiveState(nextStockState);
    this.currentState = nextLiveStockState;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public BigDecimal getCurrentPrice()
  {
    return currentPrice;
  }

  public LiveStockState getCurrentState()
  {
    return currentState;
  }
}