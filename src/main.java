import business.services.StockAlertService;
import business.services.StockBankruptService;
import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import domain.OwnedStock;
import domain.Portfolio;
import domain.Stock;
import javafx.collections.ListChangeListener;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.fileImplementation.OwnedStockFileDAO;
import persistence.fileImplementation.PortfolioFileDAO;
import persistence.fileImplementation.StockFileDAO;
import persistence.fileImplementation.StockPriceHistoryFileDAO;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.math.BigDecimal;
import java.util.UUID;

public class main
{
  public static void main(String[] args)
  {
    AppConfig config = AppConfig.getInstance();
    Logger logger = Logger.getInstance();

    // --- Shared UoW and DAOs ---
    FileUnitOfWork uow = new FileUnitOfWork(config.getDataDirectory());
    StockDAO stockDAO = new StockFileDAO(uow);
    StockPriceHistoryDAO stockPriceHistoryDAO = new StockPriceHistoryFileDAO(uow);
    OwnedStockDAO ownedStockDAO = new OwnedStockFileDAO(uow);
    PortfolioDAO portfolioDAO = new PortfolioFileDAO(uow);

    // --- Seed: create a portfolio and an OwnedStock for AAPL ---
    FileUnitOfWork seedUow = new FileUnitOfWork(config.getDataDirectory());
    seedUow.begin();
    Portfolio portfolio = Portfolio.createNew(BigDecimal.valueOf(10_000));
    new PortfolioFileDAO(seedUow).create(portfolio);
    new OwnedStockFileDAO(seedUow).create(
        OwnedStock.createNew(portfolio.getId(), "AAPL", 10));
    seedUow.commit();
    System.out.println("[SEED] Portfolio " + portfolio.getId() + " created with 10 shares of AAPL");

    // --- StockListenerService (persists price updates, exposes observable list) ---
    StockListenerService listenerService = new StockListenerService(stockPriceHistoryDAO, stockDAO, uow);

    listenerService.stocksProperty().addListener((ListChangeListener<? super business.dto.StockDTO>) change ->
    {
      System.out.println("--- UI notified: stock list updated ---");
      listenerService.getStocks().forEach(s ->
          System.out.println("  " + s.symbol() + " | " + s.currentPrice() + " | " + s.state()));
    });

    // --- StockBankruptService (handles one stock bankruptcy at a time) ---
    FileUnitOfWork bankruptUow = new FileUnitOfWork(config.getDataDirectory());
    StockBankruptService bankruptService = new StockBankruptService(
        logger, new OwnedStockFileDAO(bankruptUow), bankruptUow);

    // --- StockAlertService (observes market, fires alerts & delegates bankruptcy) ---
    // Alert if price >= 3000 (high) or <= 50 (low)
    StockAlertService alertService = new StockAlertService(
        bankruptService,
        BigDecimal.valueOf(3000),
        BigDecimal.valueOf(50));

    // --- Register observers ---
    StockMarket market = StockMarket.getInstance();
    market.addExistingStock(Stock.createNew("AAPL", BigDecimal.valueOf(150.00)));
    market.addExistingStock(Stock.createNew("GOOGL", BigDecimal.valueOf(2800.00)));
    market.addExistingStock(Stock.createNew("TSLA", BigDecimal.valueOf(700.00)));

    market.addObserver(listenerService);
    market.addObserver(alertService);

    // --- Run the ticker for 10 seconds ---
    MarketTickerThread tickerThread = new MarketTickerThread();
    tickerThread.start();

    try {
      Thread.sleep(10_000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    tickerThread.stopThread();
    try {
      tickerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // --- Final state: show remaining owned stocks ---
    System.out.println("\n--- Final owned stocks (after potential bankruptcy) ---");
    FileUnitOfWork readUow = new FileUnitOfWork(config.getDataDirectory());
    readUow.begin();
    new OwnedStockFileDAO(readUow).getAll()
        .forEach(os -> System.out.println("  " + os.getStockSymbol()
            + " | shares: " + os.getNumberOfShares()
            + " | portfolioId: " + os.getPortfolioId()));
    readUow.commit();
  }
}


