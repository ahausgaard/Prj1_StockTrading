//import business.services.StockListenerService;
//import business.stockmarket.MarketTickerThread;
//import business.stockmarket.StockMarket;
import domain.Stock;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.fileImplementation.StockFileDAO;
//import persistence.fileImplementation.StockPriceHistoryFileDAO;
import persistence.interfaces.StockDAO;
//import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;
import shared.configuration.AppConfig;

import java.math.BigDecimal;
import java.util.List;

public class main
{
  public static void main(String[] args)
  {
    UnitOfWork uow = new FileUnitOfWork(AppConfig.getInstance().getDataDirectory());
    StockDAO stockDAO = new StockFileDAO(uow);

    // --- CREATE ---
    System.out.println("=== CREATE ===");
    uow.begin();
    stockDAO.create(Stock.createNew("TEST", BigDecimal.valueOf(99.99)));
    uow.commit();
    System.out.println("Created TEST stock.");

    // --- GET ALL ---
    System.out.println("\n=== GET ALL (after create) ===");
    uow.begin();
    for (Stock s : stockDAO.getAll())
      System.out.println(s.getSymbol() + " | " + s.getCurrentState() + " | " + s.getCurrentPrice());

    // --- GET BY SYMBOL ---
    System.out.println("\n=== GET BY SYMBOL ===");
    Stock found = stockDAO.getBySymbol("TEST");
    System.out.println(found != null ? "Found: " + found.getSymbol() + " @ " + found.getCurrentPrice() : "Not found.");

    // --- UPDATE ---
    System.out.println("\n=== UPDATE ===");
    uow.begin();
    stockDAO.update(Stock.createNew("TEST", BigDecimal.valueOf(123.45)));
    uow.commit();
    uow.begin();
    Stock updated = stockDAO.getBySymbol("TEST");
    System.out.println("Updated TEST price: " + (updated != null ? updated.getCurrentPrice() : "not found"));

    // --- DELETE ---
    System.out.println("\n=== DELETE ===");
    uow.begin();
    stockDAO.delete("TEST");
    uow.commit();
    uow.begin();
    List<Stock> remaining = stockDAO.getAll();
    System.out.println("Stocks after delete:");
    if (remaining.isEmpty())
      System.out.println("(none)");
    else
      remaining.forEach(s -> System.out.println(s.getSymbol() + " | " + s.getCurrentPrice()));

//    Logger logger = Logger.getInstance();
//    StockMarket market = StockMarket.getInstance();
//    market.addExistingStock(Stock.createNew("AAPL", BigDecimal.valueOf(150.00)));
//    market.addExistingStock(Stock.createNew("GOOGL", BigDecimal.valueOf(2800.00)));
//    market.addExistingStock(Stock.createNew("TSLA", BigDecimal.valueOf(700.00)));
//
//    UnitOfWork uow = new FileUnitOfWork(AppConfig.getInstance().getDataDirectory());
//    StockDAO stockDAO = new StockFileDAO(uow);
//    StockPriceHistoryDAO stockPriceHistoryDAO = new StockPriceHistoryFileDAO(uow);
//
//    StockListenerService listenerService = new StockListenerService(stockPriceHistoryDAO, stockDAO, uow);
//    market.addObserver(listenerService);
//
//    MarketTickerThread tickerThread = new MarketTickerThread();
//    tickerThread.start();
//
//    try {
//      Thread.sleep(5000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//
//    tickerThread.stopThread();
//    try {
//      tickerThread.join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
  }
}
