package business.services;

import business.stockmarket.StockMarketObserver;
import domain.Stock;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.util.List;

public class StockListenerService implements StockMarketObserver
{
  private final Logger logger;

  public StockListenerService()
  {
    this.logger = Logger.getInstance();
  }

  @Override
  public void update(List<Stock> updatedStocks)
  {
    for (Stock stock : updatedStocks)
    {
      logger.log(LoggerLevel.INFO,
          "Stock update — Symbol: " + stock.getSymbol()
          + " | Price: " + stock.getCurrentPrice()
          + " | State: " + stock.getCurrentState());
    }
  }
}
