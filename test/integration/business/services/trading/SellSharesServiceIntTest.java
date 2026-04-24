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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
