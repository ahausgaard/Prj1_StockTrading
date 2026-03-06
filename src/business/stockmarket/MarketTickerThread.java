package business.stockmarket;

import shared.configuration.AppConfig;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

public class MarketTickerThread extends Thread
{
  private final StockMarket stockMarket;
  private final Logger logger;
  private final int updateFrequencyInMs;
  private int currentTick = 0;
  private boolean running = false;

  public MarketTickerThread()
  {
    this.stockMarket = StockMarket.getInstance();
    this.updateFrequencyInMs = AppConfig.getInstance().getUpdateFrequencyInMs();
    this.logger = Logger.getInstance();
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
        currentTick++;
        logger.log(LoggerLevel.INFO, "Market updated. Current tick: " + currentTick);
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

  public void stopTicker()
  {
    running = false;
    interrupt();
  }

  public boolean isRunning()
  {
    return running;
  }
}
