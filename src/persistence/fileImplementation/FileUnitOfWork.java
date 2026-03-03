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

  private static final Object FILE_WRITE_LOCK = new Object();

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    ensureFilesExist(directoryPath);
  }

  public synchronized List<Stock> getStocks()
  {
    if (stocks == null)
      loadStocks();
    return stocks;
  }

  public synchronized List<OwnedStock> getOwnedStocks()
  {
    if (ownedStocks == null)
      loadOwnedStocks();
    return ownedStocks;
  }

  public synchronized List<Portfolio> getPortfolios()
  {
    if (portfolios == null)
      loadPortfolios();
    return portfolios;
  }


  public synchronized List<StockPriceHistory> getStockPriceHistories()
  {
    if (stockPriceHistories == null)
      loadStockPriceHistories();
    return stockPriceHistories;
  }

  public synchronized List<Transaction> getTransactions()
  {
    if (transactions == null)
      loadTransactions();
    return transactions;
  }

  @Override public void begin()
  {
    clearData();
  }

  @Override public void commit()
  {
    synchronized (FILE_WRITE_LOCK)
    {
      if (stocks != null)
      {
        List<String> lines = new ArrayList<>();
        for (Stock stock : stocks)
        {
          lines.add(writeStockPSV(stock));
        }
        writeAllLines(directoryPath + "stocks.txt", lines);
      }

      if (ownedStocks != null)
      {
        List<String> lines = new ArrayList<>();
        for (OwnedStock ownedStock : ownedStocks) {
          lines.add(writeOwnedStockPSV(ownedStock));
        }
        writeAllLines(directoryPath + "ownedStocks.txt", lines);
      }

      if (portfolios != null)
      {
        List<String> lines = new ArrayList<>();
        for (Portfolio portfolio : portfolios) {
          lines.add(writePortfolioPSV(portfolio));
        }
        writeAllLines(directoryPath + "portfolios.txt", lines);
      }

      if (stockPriceHistories != null)
      {
        List<String> lines = new ArrayList<>();
        for (StockPriceHistory history : stockPriceHistories) {
          lines.add(writeStockPriceHistoriesPSV(history));
        }
        writeAllLines(directoryPath + "stockPriceHistories.txt", lines);
      }

      if (transactions != null)
      {
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactions) {
          lines.add(writeTransactionsPSV(transaction));
        }
        writeAllLines(directoryPath + "transactions.txt", lines);
      }
    }

    clearData();
  }

  @Override public void rollback()
  {
    clearData();
  }
  
  private List<String> readAllLines(String filePath)
  {
    try
    {
      return Files.readAllLines(Paths.get(filePath));
    }
    catch(IOException e)
    {
      Logger.getInstance().log(LoggerLevel.ERROR, "Failed to read from file: " + filePath, e);
      throw new RuntimeException("Failed to read from file: " + filePath, e);
    }
  }

  private void writeAllLines(String filePath, List<String> lines)
  {
    try
    {
      Files.write(Paths.get(filePath), lines);
    }
    catch (IOException e)
    {
      Logger.getInstance().log(LoggerLevel.ERROR, "Failed to write to file: " + filePath, e);
      throw new RuntimeException("Failed to write to file: " + filePath, e);
    }
  }

  private String writeStockPSV(Stock stock)
  {
    return
        stock.getSymbol() + "|" +
        stock.getName() + "|" +
        stock.getCurrentState() + "|" +
        stock.getCurrentPrice();
  }

  private String writeOwnedStockPSV(OwnedStock ownedStock)
  {
    return
        ownedStock.getId() + "|" +
        ownedStock.getPortfolioId() + "|" +
        ownedStock.getStockSymbol() + "|" +
        ownedStock.getNumberOfShares();
  }

  private String writePortfolioPSV(Portfolio portfolio)
  {
    return
        portfolio.getId() + "|" +
        portfolio.getCurrentBalance();
  }

  private String writeStockPriceHistoriesPSV(StockPriceHistory stockPriceHistory)
  {
    return
        stockPriceHistory.getId() + "|" +
        stockPriceHistory.getStockSymbol() + "|" +
        stockPriceHistory.getPrice()+ "|" +
        stockPriceHistory.getTimestamp();
  }

  private String writeTransactionsPSV(Transaction transaction)
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

  private Stock readStockPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Stock(parts[0], parts[1], StockState.valueOf(parts[2]), new BigDecimal(parts[3]));
  }

  private OwnedStock readOwnedStockPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new OwnedStock(UUID.fromString(parts[0]), UUID.fromString(parts[1]), parts[2], Double.parseDouble(parts[3]));
  }

  private Portfolio readPortfolioPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Portfolio(UUID.fromString(parts[0]), new BigDecimal(parts[1]));
  }

  private StockPriceHistory readStockPriceHistoryPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new StockPriceHistory(UUID.fromString(parts[0]), parts[1], new BigDecimal(parts[2]), Instant.parse(parts[3]));
  }

  private Transaction readTransactionPSV(String psv)
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

  private void loadStocks()
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

  private void loadOwnedStocks()
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

  private void loadPortfolios()
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

  private void loadStockPriceHistories()
  {
    stockPriceHistories = new ArrayList<>();
    String filePath = directoryPath + "stockPriceHistories.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        stockPriceHistories.add(readStockPriceHistoryPSV(line));
      }
    }
  }

  private void loadTransactions()
  {
    transactions = new ArrayList<>();
    String filePath = directoryPath + "transactions.txt";
    List<String> lines = readAllLines(filePath);

    for (String line : lines)
    {
      if (line != null && !line.trim().isEmpty())
      {
        transactions.add(readTransactionPSV(line));
      }
    }
  }

  private void clearData()
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
        logger.log(LoggerLevel.ERROR, "Failed to create file: " + path);
        throw new RuntimeException("Failed to create file: " + path, e);
      }
    }
  }
}
