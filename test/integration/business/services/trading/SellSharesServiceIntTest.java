package integration.business.services.trading;

import business.services.trading.BuySharesService;
import domain.Portfolio;
import domain.Stock;
import org.junit.jupiter.api.AfterEach;
import persistence.fileImplementation.*;

import java.io.File;
import java.io.IOException;
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
  private BuySharesService service;
  private Portfolio portfolio;
  private Stock stock;

  @AfterEach void cleanup() throws IOException
  {
    Files.walk(tempDir)
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }
}
