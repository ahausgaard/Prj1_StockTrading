package integration.business.services.trading;

import business.commands.BuySharesRequest;
import business.services.trading.BuySharesService;
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

public class BuySharesServiceIntTest
{
  private Path tempDir;
  private FileUnitOfWork uow;
  private StockFileDAO stockDAO;
  private OwnedStockFileDAO ownedStockDAO;
  private PortfolioFileDAO portfolioDAO;
  private TransactionFileDAO transactionDAO;
  private BuySharesService service;
  private Portfolio portfolio;
  private Stock stock;


  @BeforeEach void setup() throws IOException
  {
    tempDir = Files.createTempDirectory("buy_shares_int_test");
    uow = new FileUnitOfWork(tempDir.toString() + File.separator);

    stockDAO = new StockFileDAO(uow);
    portfolioDAO = new PortfolioFileDAO(uow);
    ownedStockDAO = new OwnedStockFileDAO(uow);
    transactionDAO = new TransactionFileDAO(uow);
    service = new BuySharesService(stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow);

    //Initial data
    stock = Stock.createNew("PNDORA", new BigDecimal("100.00"));
    portfolio = Portfolio.createNew(new BigDecimal("10000.00"));

    uow.begin();
    stockDAO.create(stock);
    portfolioDAO.create(portfolio);
    uow.commit();
  }

  @AfterEach void cleanup() throws IOException
  {
    Files.walk(tempDir)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  @Test void buyShares_validPurchase_persistsOwnedStockDeductsBalance() throws IOException
  {
    BuySharesRequest request = new BuySharesRequest(portfolio.getId(), "PNDORA", 10);
    service.buyShares(request);

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    //OwnedStock written to file
    assertEquals(1, ownedStocks.size());
    assertEquals("PNDORA", ownedStocks.getFirst().getStockSymbol());
    assertEquals(10, ownedStocks.getFirst().getQuantity());

    //Balance deduction
    assertTrue(updatedPortfolio.getCurrentBalance().compareTo(new BigDecimal("10000.00")) < 0);

    //Record of transaction
    assertEquals(1, transactions.size());
    assertEquals(TransactionType.BUY, transactions.getFirst().getType());
  }

  @Test void buyShares_insufficientFunds_fails() throws IOException
  {
    BuySharesRequest request = new BuySharesRequest(portfolio.getId(), "PNDORA", 200);

    assertThrows(IllegalStateException.class, () -> service.buyShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(0, ownedStocks.size(), "No shares should be bought due to insufficient funds.");
    assertEquals(new BigDecimal("10000.00"), updatedPortfolio.getCurrentBalance(), "Portfolio balance should remain unchanged after failed purchase.");
    assertEquals(0, transactions.size(), "No transaction should be recorded for failed purchase.");
  }

  @Test void buyShares_invalidQuantity_fail() throws IOException
  {
    BuySharesRequest request = new BuySharesRequest(portfolio.getId(), "PNDORA", -1);

    assertThrows(IllegalArgumentException.class, () -> service.buyShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(0, ownedStocks.size());
    assertEquals(new BigDecimal("10000.00"), updatedPortfolio.getCurrentBalance());
    assertEquals(0, transactions.size());
  }

  @Test void buyShares_stockNotFound_fail() throws IOException
  {
    BuySharesRequest request = new BuySharesRequest(portfolio.getId(), "AAPL", 1);

    assertThrows(IllegalArgumentException.class, () -> service.buyShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(0, ownedStocks.size());
    assertEquals(new BigDecimal("10000.00"), updatedPortfolio.getCurrentBalance());
    assertEquals(0, transactions.size());
  }

  @Test void buyShares_bankruptStock_fail() throws IOException
  {
    //Seed bankrupt stock
    Stock bankruptStock = Stock.createFromStorage("BWE", StockState.BANKRUPT, new BigDecimal("50.00"));
    uow.begin();
    stockDAO.create(bankruptStock);
    uow.commit();

    BuySharesRequest request = new BuySharesRequest(portfolio.getId(), "BWE", 1);

    assertThrows(IllegalStateException.class, () -> service.buyShares(request));

    uow.begin();
    List<OwnedStock> ownedStocks = ownedStockDAO.getAll();
    Portfolio updatedPortfolio = portfolioDAO.getById(portfolio.getId());
    List<Transaction> transactions = transactionDAO.getAll();
    uow.commit();

    assertEquals(0, ownedStocks.size());
    assertEquals(new BigDecimal("10000.00"), updatedPortfolio.getCurrentBalance());
    assertEquals(0, transactions.size());
  }






}
