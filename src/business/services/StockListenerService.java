package business.services;

import business.observer.StockDTO;
import business.observer.StockUpdateEvent;
import business.observer.StockMarketObserver;
import domain.Stock;
import domain.StockPriceHistory;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockListenerService implements StockMarketObserver
{
  private final Logger logger;
  private final StockPriceHistoryDAO stockPriceHistoryDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final List<StockDTO> stocks = new ArrayList<>();
  private Runnable onStocksUpdated;

  public void setOnStocksUpdated(Runnable callback) {
    this.onStocksUpdated = callback;
  }

  public List<StockDTO> getStocks() {
    return Collections.unmodifiableList(stocks);
  }

  public StockListenerService(StockPriceHistoryDAO stockPriceHistoryDAO, StockDAO stockDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockPriceHistoryDAO = stockPriceHistoryDAO;
    this.stockDAO = stockDAO;
    this.uow = uow;
  }


  @Override
  public void update(StockUpdateEvent event)
  {
    Instant now = Instant.now();

    event.stocks().forEach(stock ->
    {
      try
      {
        uow.begin();

        logger.log(LoggerLevel.INFO,
            "Stock update — Symbol: " + stock.symbol()
            + " | Price: " + stock.currentPrice()
            + " | State: " + stock.state());

        Stock existing = stockDAO.getBySymbol(stock.symbol());
        if (existing == null)
        {
          stockDAO.create(Stock.createNew(stock.symbol(), stock.currentPrice()));
        }
        else
        {
          stockDAO.update(Stock.createFromStorage(stock.symbol(), stock.state(), stock.currentPrice()));
        }

        // Record price history
        stockPriceHistoryDAO.create(StockPriceHistory.createNew(
            stock.symbol(),
            stock.currentPrice(),
            now));

        uow.commit();
      }
      catch (Exception e)
      {
        uow.rollback();
        logger.log(LoggerLevel.WARNING, "Failed to persist stock update for: " + stock.symbol(), e);
      }
    });


    stocks.clear();
    stocks.addAll(event.stocks());
    if (onStocksUpdated != null)
      onStocksUpdated.run();
  }
}
