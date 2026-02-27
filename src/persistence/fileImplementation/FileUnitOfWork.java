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
import java.util.ArrayList;
import java.util.List;

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

  public List<OwnedStock> getOwnedStocks()
  {
    if (ownedStocks == null)
      ownedStocks = new ArrayList<OwnedStock>();
    return ownedStocks;
  }

  public List<Portfolio> getPortfolios()
  {
    return portfolios;
  }

  public List<Stock> getStocks()
  {
    return stocks;
  }

  public List<StockPriceHistory> getStockPriceHistories()
  {
    return stockPriceHistories;
  }

  public List<Transaction> getTransactions()
  {
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

  public String toPSV(Stock stock)
  {
    return stock.getSymbol() + "|" + stock.getName() + "|" + stock.getCurrentState() + "|" + stock.getCurrentPrice();
  }

  public Stock fromPSV(String psv)
  {
    String[] parts = psv.split("\\|");
    return new Stock(parts[0], parts[1], StockState.valueOf(parts[2]), new BigDecimal(parts[3]));
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
