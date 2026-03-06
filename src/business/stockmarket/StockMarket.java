package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import business.stockmarket.simulation.LiveStockState;
import domain.Stock;
import domain.StockState;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;
import java.util.List;

public class StockMarket
{
  private final Logger logger = Logger.getInstance();
  private List< LiveStock> liveStocks;

  public void addNewStock(String stockSymbol)
  {
    liveStocks.add(LiveStock.createNew(stockSymbol.toUpperCase()));
    logger.log(LoggerLevel.INFO, "Added new stock: " + stockSymbol.toUpperCase());
  }

  public void addExistingStock(Stock stock)
  {
    liveStocks.add(LiveStock.fromExisting(stock));
  }

}
