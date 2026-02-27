package persistence.fileImplementation;

import domain.*;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUnitOfWork implements UnitOfWork
{
  private final String directoryPath;
  private List<OwnedStock> ownedStocks;
  private List<Portfolio> portfolios;
  private List<Stock> stocks;
  private List<StockPriceHistory> stockPriceHistories;
  private List<Transaction> transactions;

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    ensureFilesExist(directoryPath);
  }

  public List<Stock> getStocks()
  {
    if (stocks == null)
      loadStocks();
    return stocks;
  }

  public List<OwnedStock> getOwnedStocks()
  {
    if (ownedStocks == null)
      loadOwnedStocks();
    return ownedStocks;
  }

  public List<Portfolio> getPortfolios()
  {
    if (portfolios == null)
      loadPortfolios();
    return portfolios;
  }


  public List<StockPriceHistory> getStockPriceHistories()
  {
    if (stockPriceHistories == null)
      loadStockPriceHistories();
    return stockPriceHistories;
  }

  public List<Transaction> getTransactions()
  {
    if (transactions == null)
      loadTransactions();
    return transactions;
  }

  @Override public void begin()
  {
    resetLists();
  }

  @Override public void commit()
  {

  }

  @Override public void rollback()
  {
    resetLists();
  }
  public List<String> readAllLines(String filePath)
  {
    try
    {
      return Files.readAllLines(Paths.get(filePath));
    }
    catch(IOException e)
    {
      throw new RuntimeException("Failed to read from file: " + filePath, e);
    }
  }

  public String writeStockPSV(Stock stock)
  {
    return
        stock.getSymbol() + "|" +
        stock.getName() + "|" +
        stock.getCurrentState() + "|" +
        stock.getCurrentPrice();
  }

  public String writeOwnedStockPSV(OwnedStock ownedStock)
  {
    return
        ownedStock.getId() + "|" +
        ownedStock.getPortfolioId() + "|" +
        ownedStock.getStockSymbol() + "|" +
        ownedStock.getNumberOfShares();
  }

  public String writePortfolioPSV(Portfolio portfolio)
  {
    return
        portfolio.getId() + "|" +
        portfolio.getCurrentBalance();
  }

  public String writeStockPriceHistoriesPSV(StockPriceHistory stockPriceHistory)
  {
    return
        stockPriceHistory.getId() + "|" +
        stockPriceHistory.getStockSymbol() + "|" +
        stockPriceHistory.getPrice()+ "|" +
        stockPriceHistory.getTimestamp();
  }

  public String writeTransactionsPSV(Transaction transaction)
  {
    return
        transaction.getId() + "|" +
        transaction.getPortfolioId() + "|" +
        transaction.getType() + "|" +
        transaction.getStockSymbol() + "|" +
        transaction.getQuantity() + "|" +
        transaction.getPricePerShare() + "|" +
        transaction.getTotalAmount() + "|" +
        transaction.getFee() + "|" +
        transaction.getTimestamp();
  }

  public Stock readStockPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Stock(parts[0], parts[1], StockState.valueOf(parts[2]), new BigDecimal(parts[3]));
  }

  public OwnedStock readOwnedStockPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new OwnedStock(UUID.fromString(parts[0]), UUID.fromString(parts[1]), parts[2], Double.parseDouble(parts[3]));
  }

  public Portfolio readPortfolioPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Portfolio(UUID.fromString(parts[0]), new BigDecimal(parts[1]));
  }

  public StockPriceHistory readStockPriceHistoryPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new StockPriceHistory(UUID.fromString(parts[0]), parts[1], new BigDecimal(parts[3]), Instant.parse(parts[4]));
  }

  public Transaction readTransactionPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Transaction(UUID.fromString(parts[0]),
        UUID.fromString(parts[1]),
        TransactionType.valueOf(parts[2]),
        parts[3],
        new BigDecimal(parts[4]),
        new BigDecimal(parts[5]),
        new BigDecimal(parts[6]),
        new BigDecimal(parts[7]),
        Instant.parse(parts[8]));
  }

  public void loadStocks()
  {
    stocks = new ArrayList<>();
    String filePath = directoryPath + "stocks.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line!= null && !line.trim().isEmpty())
      {
        Stock stock = readStockPSV(line);
        stocks.add(stock);
      }
    }
  }

  public void loadOwnedStocks()
  {
    ownedStocks = new ArrayList<>();
    String filePath = directoryPath + "ownedStocks.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        ownedStocks.add(readOwnedStockPSV(line));
      }
    }
  }

  public void loadPortfolios()
  {
    portfolios = new ArrayList<>();
    String filePath = directoryPath + "portfolios.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        portfolios.add(readPortfolioPSV(line));
      }
    }
  }

  public void loadStockPriceHistories()
  {
    stockPriceHistories = new ArrayList<>();
    String filePath = directoryPath + "stockPriceHistories.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        // Using your existing method name
        stockPriceHistories.add(readStockPriceHistoryPSV(line));
      }
    }
  }

  public void loadTransactions()
  {
    transactions = new ArrayList<>();
    String filePath = directoryPath + "transactions.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        // Using your existing method name
        transactions.add(readTransactionPSV(line));
      }
    }
  }



  private void resetLists()
  {
    stocks = null;
    ownedStocks = null;
    portfolios = null;
    stockPriceHistories = null;
    transactions = null;
  }

  private void ensureFilesExist(String directoryPath)
  {
    final Logger logger = Logger.getInstance();
    List<String> files = List.of(
        directoryPath + "stocks.txt",
        directoryPath + "ownedStocks.txt",
        directoryPath + "portfolios.txt",
        directoryPath + "stockPriceHistories.txt",
        directoryPath + "transactions.txt");

    for (String path : files)
    {
      try
      {
        Path filepath = Paths.get(path);
        if (!Files.exists(filepath))
          Files.createFile(filepath);
      }
      catch (IOException e)
      {
        logger.log(LoggerLevel.ERROR, "Failed to create file: " + path, e);
        throw new RuntimeException("Failed to create file: " + path, e);
      }
    }
  }
}
