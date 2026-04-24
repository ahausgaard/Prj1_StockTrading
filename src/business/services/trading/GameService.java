package business.services.trading;

import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import domain.Portfolio;
import domain.Stock;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;
import java.util.List;

public class GameService
{
  private final Logger logger;
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final StockPriceHistoryDAO stockPriceHistoryDAO;
  private final UnitOfWork uow;
  private final StockMarket stockMarket;
  private MarketTickerThread marketTickerThread;

  private static final String[] DEFAULT_STOCK_SYMBOLS = {"AAPL", "GOOGL", "TSLA", "AMZN", "MSFT"};

  public GameService(StockDAO stockDAO, PortfolioDAO portfolioDAO,
      OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO,
      StockPriceHistoryDAO stockPriceHistoryDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.stockPriceHistoryDAO = stockPriceHistoryDAO;
    this.uow = uow;
    this.stockMarket = StockMarket.getInstance();
  }

  public void startGame()
  {
    logger.log(LoggerLevel.INFO, "Starting game...");

    uow.begin();
    try
    {
      List<Stock> existingStocks = stockDAO.getAll();

      if (existingStocks.isEmpty())
      {
        initializeDefaultStocks();
        initializeDefaultPortfolio();
        uow.commit();

        // Add newly created stocks to the live market
        for (String symbol : DEFAULT_STOCK_SYMBOLS)
        {
          stockMarket.addNewStock(symbol);
        }
      }
      else
      {
        uow.commit();

        // Load persisted stocks into the live market
        for (Stock stock : existingStocks)
        {
          stockMarket.addExistingStock(stock);
        }
      }

      startMarketTicker();
      logger.log(LoggerLevel.INFO, "Game started successfully.");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR, "Failed to start game: " + e.getMessage());
      throw e;
    }
  }

  public void shutdown()
  {
    logger.log(LoggerLevel.INFO, "Shutting down...");
    try
    {
      saveGame();
    }
    catch (Exception e)
    {
      logger.log(LoggerLevel.ERROR, "Error during shutdown save: " + e.getMessage());
      stopMarketTicker();
    }
  }

  public void saveGame()
  {
    logger.log(LoggerLevel.INFO, "Saving game...");

    stopMarketTicker();

    uow.begin();
    try
    {
      List<Stock> marketStocks = stockMarket.getAllStocks();
      for (Stock stock : marketStocks)
      {
        Stock existing = stockDAO.getBySymbol(stock.getSymbol());
        if (existing == null)
        {
          stockDAO.create(stock);
        }
        else
        {
          stockDAO.update(stock);
        }
      }

      uow.commit();
      logger.log(LoggerLevel.INFO, "Game saved successfully.");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR, "Failed to save game: " + e.getMessage());
      throw e;
    }
  }


  public void loadGame()
  {
    logger.log(LoggerLevel.INFO, "Loading game...");

    stopMarketTicker();
    stockMarket.clearStocks();

    uow.begin();
    try
    {
      List<Stock> savedStocks = stockDAO.getAll();
      uow.commit();

      if (savedStocks.isEmpty())
      {
        logger.log(LoggerLevel.WARNING,
            "No saved game found. Starting a new game instead.");
        startGame();
        return;
      }

      for (Stock stock : savedStocks)
      {
        stockMarket.addExistingStock(stock);
      }

      startMarketTicker();
      logger.log(LoggerLevel.INFO,
          "Game loaded successfully with " + savedStocks.size() + " stocks.");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR, "Failed to load game: " + e.getMessage());
      throw e;
    }
  }

  public void restartGame()
  {
    logger.log(LoggerLevel.INFO, "Restarting game...");

    stopMarketTicker();
    stockMarket.clearStocks();

    uow.begin();
    try
    {
      clearAllPersistedData();
      uow.commit();
      logger.log(LoggerLevel.INFO, "Game data cleared. Starting fresh...");
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR,
          "Failed to restart game: " + e.getMessage());
      throw e;
    }

    startGame();
  }

  // Helper functions

  private void initializeDefaultStocks()
  {
    BigDecimal defaultPrice = AppConfig.getInstance().getStockResetValue();

    for (String symbol : DEFAULT_STOCK_SYMBOLS)
    {
      Stock stock = Stock.createNew(symbol, defaultPrice);
      stockDAO.create(stock);
      logger.log(LoggerLevel.INFO,
          "Initialized stock: " + symbol + " at price " + defaultPrice);
    }
  }

  private void initializeDefaultPortfolio()
  {
    List<Portfolio> existingPortfolios = portfolioDAO.getAll();
    if (existingPortfolios.isEmpty())
    {
      BigDecimal startingBalance =
          AppConfig.getInstance().getStartingBalance();
      Portfolio portfolio = Portfolio.createNew(startingBalance);
      portfolioDAO.create(portfolio);
      logger.log(LoggerLevel.INFO,
          "Created default portfolio with balance: " + startingBalance);
    }
  }

  private void clearAllPersistedData()
  {
    for (var ownedStock : ownedStockDAO.getAll())
    {
      ownedStockDAO.delete(ownedStock.getStockSymbol());
    }

    for (var transaction : transactionDAO.getAll())
    {
      transactionDAO.delete(transaction.getId());
    }

    for (var history : stockPriceHistoryDAO.getAll())
    {
      stockPriceHistoryDAO.delete(history.getId());
    }

    for (var portfolio : portfolioDAO.getAll())
    {
      portfolioDAO.delete(portfolio.getId());
    }

    for (var stock : stockDAO.getAll())
    {
      stockDAO.delete(stock.getSymbol());
    }
  }

  private void startMarketTicker()
  {
    if (marketTickerThread == null || !marketTickerThread.isRunning())
    {
      marketTickerThread = new MarketTickerThread();
      marketTickerThread.start();
    }
  }

  private void stopMarketTicker()
  {
    if (marketTickerThread != null && marketTickerThread.isRunning())
    {
      marketTickerThread.stopThread();
      marketTickerThread = null;
    }
  }
}
