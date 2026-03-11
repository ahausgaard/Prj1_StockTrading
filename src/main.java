import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import domain.Stock;
import javafx.collections.ListChangeListener;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.fileImplementation.StockFileDAO;
import persistence.fileImplementation.StockPriceHistoryFileDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;
import shared.configuration.AppConfig;

import java.math.BigDecimal;

public class main
{
  public static void main(String[] args)
  {
    StockMarket market = StockMarket.getInstance();
    market.addExistingStock(Stock.createNew("AAPL", BigDecimal.valueOf(150.00)));
    market.addExistingStock(Stock.createNew("GOOGL", BigDecimal.valueOf(2800.00)));
    market.addExistingStock(Stock.createNew("TSLA", BigDecimal.valueOf(700.00)));

    FileUnitOfWork uow = new FileUnitOfWork(AppConfig.getInstance().getDataDirectory());
    StockDAO stockDAO = new StockFileDAO(uow);
    StockPriceHistoryDAO stockPriceHistoryDAO = new StockPriceHistoryFileDAO(uow);

    StockListenerService listenerService = new StockListenerService(stockPriceHistoryDAO, stockDAO, uow);


    listenerService.stocksProperty().addListener((ListChangeListener<? super business.dto.StockDTO>) change ->
    {
      System.out.println("--- UI notified: stock list updated ---");
      listenerService.getStocks().forEach(s ->
          System.out.println("  " + s.symbol() + " | " + s.currentPrice() + " | " + s.state()));
    });

    market.addObserver(listenerService);

    MarketTickerThread tickerThread = new MarketTickerThread();
    tickerThread.start();

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    tickerThread.stopThread();
    try {
      tickerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}


