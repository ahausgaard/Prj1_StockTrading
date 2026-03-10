package business.stockmarket;

import shared.configuration.AppConfig;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

public class MarketTickerThread extends Thread
{
  private final StockMarket stockMarket;
  private final Logger logger;
  private final int updateFrequencyInMs;
  private volatile boolean running = false;

  public MarketTickerThread()
  {
    this.logger = Logger.getInstance();
    this.stockMarket = StockMarket.getInstance();
    this.updateFrequencyInMs = AppConfig.getInstance().getUpdateFrequencyInMs();
  }

  @Override public void run()
  {
    running = true;
    logger.log(LoggerLevel.INFO, "Market ticker started.");
    while (running)
    {
      try
      {
        Thread.sleep(updateFrequencyInMs);
        stockMarket.updateAllLiveStocks();
        logger.log(LoggerLevel.INFO, "Market updated.");
      }
      catch (InterruptedException e)
      {
        logger.log(LoggerLevel.WARNING, "Market ticker interrupted.", e);
        Thread.currentThread().interrupt();
        running = false;
      }
    }
    logger.log(LoggerLevel.INFO, "Market ticker stopped.");
  }

  public boolean isRunning()
  {
    return running;
  }

  public void stopThread()
  {
    running = false;
    interrupt();
  }

}
