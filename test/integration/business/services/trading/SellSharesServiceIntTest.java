package integration.business.services.trading;

import business.commands.SellSharesRequest;
import business.services.trading.SellSharesService;
import domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.fileImplementation.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SellSharesServiceIntTest
{
  private Path tempDir;
  private FileUnitOfWork uow;
  private StockFileDAO stockDAO;
  private OwnedStockFileDAO ownedStockDAO;
  private PortfolioFileDAO portfolioDAO;
  private TransactionFileDAO transactionDAO;
  private SellSharesService service;
  private Portfolio portfolio;
  private Stock stock;
  private OwnedStock ownedStock;

  @BeforeEach void setup() throws IOException
  {
   tempDir = Files.createTempDirectory("sell_shares_int_test");
   uow = new FileUnitOfWork(tempDir.toString() + File.separator);

   stockDAO = new StockFileDAO(uow);
   portfolioDAO = new PortfolioFileDAO(uow);
   ownedStockDAO = new OwnedStockFileDAO(uow);
   transactionDAO = new TransactionFileDAO(uow);
   service = new SellSharesService(ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO);

   stock = Stock.createNew("PNDORA", new BigDecimal("100.00"));
   portfolio = Portfolio.createNew(new BigDecimal("10000.00"));
   ownedStock = OwnedStock.createNew(portfolio.getId(), "PNDORA", 10);

   uow.begin();
   stockDAO.create(stock);
   portfolioDAO.create(portfolio);
   ownedStockDAO.create(ownedStock);
   uow.commit();
  }

  @AfterEach void cleanup() throws IOException
  {
    Files.walk(tempDir)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  @Test void sellShares_validSale_persistsReducedQuantityCreditsBalance() throws IOException
  {
    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "PNDORA", 5);
    service.sellShares(request);

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    // Quantity reduced from 10 to 5
    assertEquals(1, ownedStocks.size());
    assertEquals(5, ownedStocks.getFirst().getQuantity());

    // Balance credited
    assertTrue(updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")) > 0);

    // Transaction recorded
    assertEquals(1, transactions.size());
    assertEquals(TransactionType.SELL, transactions.getFirst().getType());
  }

  @Test void sellShares_sellAllShares_deletesOwnedStockEntry() throws IOException
  {
    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "PNDORA", 10);
    service.sellShares(request);

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    // Quantity reduced from 10 to 5
    assertEquals(0, ownedStocks.size());

    // Balance credited
    assertTrue(updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")) > 0);

    // Transaction recorded
    assertEquals(1, transactions.size());
    assertEquals(TransactionType.SELL, transactions.getFirst().getType());
  }

  @Test void sellShares_sellMoreThanOwned_fails() throws IOException
  {
    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "PNDORA", 12);
    assertThrows(IllegalArgumentException.class, () -> service.sellShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(1, ownedStocks.size());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    assertEquals(0, updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")));
    assertEquals(0, transactions.size());
  }

  @Test void sellShares_stockNotFound_fails() throws IOException
  {
    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "AAPL", 1);
    assertThrows(IllegalArgumentException.class, () -> service.sellShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(1, ownedStocks.size());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    assertEquals(0, updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")));
    assertEquals(0, transactions.size());
  }

  @Test void sellShares_invalidQuantity_fails() throws IOException
  {
    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "PNDORA", -1);
    assertThrows(IllegalArgumentException.class, () -> service.sellShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(1, ownedStocks.size());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    assertEquals(0, updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")));
    assertEquals(0, transactions.size());
  }

  @Test void sellShares_stockNotOwned_fails() throws IOException
  {
    //Seed new stock
    Stock unownedStock = Stock.createNew("AAPL", new BigDecimal("150.00"));
    uow.begin();
    stockDAO.create(unownedStock);
    uow.commit();

    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "AAPL", 1);
    assertThrows(IllegalArgumentException.class, () -> service.sellShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(1, ownedStocks.size());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    assertEquals(0, updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")));
    assertEquals(0, transactions.size());
  }

  @Test void sellShares_bankrupStock_fails() throws IOException
  {
    //Seed and "buy" new stock
    Stock bankruptStock = Stock.createFromStorage("BWE", StockState.BANKRUPT, new BigDecimal("50.00"));
    OwnedStock bankruptOwnedStock = OwnedStock.createNew(portfolio.getId(), "BWE", 5);
    uow.begin();
    stockDAO.create(bankruptStock);
    ownedStockDAO.create(bankruptOwnedStock);
    uow.commit();

    SellSharesRequest request = new SellSharesRequest(portfolio.getId(), "BWE", 1);
    assertThrows(IllegalStateException.class, () -> service.sellShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(2, ownedStocks.size());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    assertEquals(0, updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")));
    assertEquals(0, transactions.size());
  }








}
