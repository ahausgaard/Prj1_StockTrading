package business.services;

import business.dto.StockDTO;
import business.dto.StockUpdateEvent;
import business.stockmarket.StockMarketObserver;
import domain.Stock;
import domain.StockPriceHistory;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.time.Instant;

public class StockListenerService implements StockMarketObserver
{
  private final Logger logger;
  private final StockPriceHistoryDAO stockPriceHistoryDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;

  private final ReadOnlyListWrapper<StockDTO> stocks =
      new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

  public StockListenerService(StockPriceHistoryDAO stockPriceHistoryDAO, StockDAO stockDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockPriceHistoryDAO = stockPriceHistoryDAO;
    this.stockDAO = stockDAO;
    this.uow = uow;
  }

  public ReadOnlyListProperty<StockDTO> stocksProperty()
  {
    return stocks.getReadOnlyProperty();
  }

  public ObservableList<StockDTO> getStocks()
  {
    return stocks.getReadOnlyProperty();
  }

  @Override
  public void update(StockUpdateEvent event)
  {
    Instant now = Instant.now();
    uow.begin();

    event.stocks().forEach(stock ->
    {
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
    });

    uow.commit();


    stocks.setAll(event.stocks());
  }
}
