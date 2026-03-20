import business.observer.StockDTO;
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


    listenerService.setOnStocksUpdated(() ->
    {
      System.out.println("Stocks updated:");
      for (StockDTO stock : listenerService.getStocks())
      {
        System.out.println("- " + stock.symbol() + ": " + stock.currentPrice() + " (" + stock.state() + ")");
      }
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


