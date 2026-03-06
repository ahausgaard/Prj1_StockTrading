import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import domain.Stock;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;

public class main
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    StockMarket market = StockMarket.getInstance();
    Stock appleStock = Stock.createNew("AAPL", "Apple Inc.",
        BigDecimal.valueOf(150.00));

    market.addExistingStock(appleStock);
    MarketTickerThread tickerThread = new MarketTickerThread();
    tickerThread.start();

    //Testing
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    tickerThread.stopThread();
    try {
      tickerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(appleStock.getCurrentPrice());
    System.out.println(appleStock.getCurrentState());
  }
}
