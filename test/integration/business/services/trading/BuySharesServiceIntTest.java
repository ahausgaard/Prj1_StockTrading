package integration.business.services.trading;

import business.services.trading.BuySharesService;
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


}
