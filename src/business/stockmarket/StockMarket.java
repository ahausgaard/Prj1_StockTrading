package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.LiveStockState;
import domain.Stock;
import domain.StockState;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StockMarket
{
  private final Logger logger = Logger.getInstance();
  private List<LiveStock> liveStocks = new ArrayList<>();

  public void addNewStock(String stockSymbol)
  {
    liveStocks.add(LiveStock.createNew(stockSymbol.toUpperCase()));
    logger.log(LoggerLevel.INFO, "Added new stock: " + stockSymbol.toUpperCase());
  }

  public void addExistingStock(Stock stock)
  {
    liveStocks.add(LiveStock.fromExisting(stock));
    logger.log(LoggerLevel.INFO, "Added existing stock: " + stock.getSymbol());
  }

  public void updateAllLiveStocks()
  {
    for (LiveStock liveStock : liveStocks)
    {
      liveStock.updatePrice();
    }
  }

  // Bill Pugh Singleton implementation
  private StockMarket() {}

  private static class StockMarketHolder {
    private static final StockMarket INSTANCE = new StockMarket();
  }

  public static StockMarket getInstance() {
    return StockMarketHolder.INSTANCE;
  }
}
