package business.services;

import business.observer.StockDTO;
import business.observer.StockUpdateEvent;
import business.observer.StockMarketObserver;
import domain.StockState;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;

public class StockAlertService implements StockMarketObserver
{
  private final Logger logger;
  private final StockBankruptService bankruptService;
  private final BigDecimal alertThresholdHigh;
  private final BigDecimal alertThresholdLow;


  public StockAlertService(StockBankruptService bankruptService,
      BigDecimal alertThresholdHigh, BigDecimal alertThresholdLow)
  {
    this.logger = Logger.getInstance();
    this.bankruptService = bankruptService;
    this.alertThresholdHigh = alertThresholdHigh;
    this.alertThresholdLow = alertThresholdLow;
  }

  //Måske lidt primitiv...
  @Override
  public void update(StockUpdateEvent event)
  {
    for (StockDTO stock : event.stocks())
    {
      if (stock.state() == StockState.BANKRUPT)
      {
        logger.log(LoggerLevel.INFO,
            "[ALERT] Stock BANKRUPT: " + stock.symbol() + " — triggering bankruptcy handling.");
        bankruptService.handleBankruptcy(stock.symbol());
      }
      else if (stock.currentPrice().compareTo(alertThresholdHigh) >= 0)
      {
        logger.log(LoggerLevel.INFO,
            "[ALERT] " + stock.symbol() + " hit HIGH threshold: " + stock.currentPrice());
      }
      else if (stock.currentPrice().compareTo(alertThresholdLow) <= 0)
      {
        logger.log(LoggerLevel.INFO,
            "[ALERT] " + stock.symbol() + " hit LOW threshold: " + stock.currentPrice());
      }
    }
  }
}
