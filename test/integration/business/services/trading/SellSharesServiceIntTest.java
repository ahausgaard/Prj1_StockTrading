package integration.business.services.trading;

import business.services.trading.SellSharesService;
import domain.OwnedStock;
import domain.Portfolio;
import domain.Stock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import persistence.fileImplementation.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

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
}
