package unit.business.services.trading;

import business.commands.BuySharesRequest;
import domain.Portfolio;
import domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.logging.Logger;
import shared.logging.LoggerLevel;
import unit.mocks.*;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


public class BuySharesServiceTest
{
  private MockStockDAO stockDAO;
  private MockOwnedStockDAO ownedStockDAO;
  private MockUnitOfWork uow;
  private MockPortfolioDAO portfolioDAO;
  private MockTransactionDAO transactionDAO;
  private BuySharesService service;
  private Logger logger;

  @BeforeEach void setup()
  {
    stockDAO = new MockStockDAO();
    ownedStockDAO = new MockOwnedStockDAO();
    uow = new MockUnitOfWork();
    portfolioDAO = new MockPortfolioDAO();
    transactionDAO = new MockTransactionDAO();
    this.logger = Logger.getInstance();

    stockDAO.setMockStock(Stock.createNew("PNDORA", new BigDecimal("10.0")));
    portfolioDAO.setMockPortfolio(
        Portfolio.createNew(new BigDecimal("10100.0")));

    service = new BuySharesService(stockDAO, ownedStockDAO, portfolioDAO,
        transactionDAO, uow);

  }

  //Zero and One
  @Test void buyShares_oneValidShare_commitsTransaction()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.buyShares(request);
    assertEquals(1, uow.getCommitCount());
  }

  @Test void buyShares_0_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 0);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.buyShares(request));
    assertEquals("Number of shares must be greater than zero.",
        exception.getMessage());
  }

  @Test void buyShares_newStock_createsOwnedStockEntry()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.buyShares(request);
    assertEquals(1, uow.getCommitCount());
    assertEquals(0, ownedStockDAO.getAll().size());
  }

  @Test void buyShares_alreadyOwnedStock_incrementsQuantityWithoutDuplicate()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.buyShares(request);
    assertEquals(1, uow.getCommitCount());
    assertEquals(1, ownedStockDAO.getAll().size());
    assertEquals(1, ownedStockDAO.getAll().get(0).getQuantity());

    service.buyShares(request);
    assertEquals(2, uow.getCommitCount());
    assertEquals(1, ownedStockDAO.getAll().size());
    assertEquals(2, ownedStockDAO.getAll().get(0).getQuantity());
  }

  //Boundaries
  @Test void buyShares_largeQuantity_updatesOwnedStockQuantity()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 100);
    service.buyShares(request);
    assertEquals(100, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void buyShares_exactBalance_zerosPortfolioBalance()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1000);
    service.buyShares(request);
    assertEquals(0, portfolioDAO.getMockPortfolio().getCurrentBalance()
        .compareTo(BigDecimal.ZERO));
    assertEquals(1000, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void buyShares_spend_1cent_too_much_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1000.0009901);
    logger.log(LoggerLevel.INFO,
        "Current portfolio balance: " + portfolioDAO.getMockPortfolio()
            .getCurrentBalance() + ", Total price: "
            + request.quantity() * 10.1);
    Exception exception = assertThrows(IllegalStateException.class,
        () -> service.buyShares(request));
    assertEquals("Insufficient funds in portfolio to complete purchase.",
        exception.getMessage());
  }

  //Interface & Exceptions
  @Test void buyShares_negative_quantity_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", -10);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.buyShares(request));
    assertEquals("Number of shares must be greater than zero.",
        exception.getMessage());
  }
  
  @Test void buyShares_bankrupt_stock_throwsException()
  {
    stockDAO.setMockStock(Stock.createFromStorage("PNDORA", domain.StockState.BANKRUPT, new BigDecimal("10.0")));

    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);

    Exception exception = assertThrows(IllegalStateException.class,
        () -> service.buyShares(request));
    assertEquals("Cannot buy shares of a bankrupt stock: PNDORA",
        exception.getMessage());
  }

  @Test void buyShares_insufficientFunds_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10000);
    Exception exception = assertThrows(IllegalStateException.class,
        () -> service.buyShares(request));
    assertEquals("Insufficient funds in portfolio to complete purchase.",
        exception.getMessage());
  }

  @Test void buyShares_with_null_symbol_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), null, 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.buyShares(request));
    assertEquals("Stock symbol must not be empty.",
        exception.getMessage());
  }

  @Test void buyShares_with_empty_string_symbol_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "", 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.buyShares(request));
    assertEquals("Stock symbol must not be empty.",
        exception.getMessage());
  }

  @Test void buyShares_symbol_not_found_throwsException()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "TEST", 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.buyShares(request));
    assertEquals("Stock with symbol TEST not found.", exception.getMessage());
  }

  //State & Behaviour
  @Test void buyShares_validPurchase_updatesPortfolioAndOwnedStock()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.buyShares(request);
    assertEquals(1, uow.getCommitCount());
    assertEquals(1, ownedStockDAO.getAll().size());
    assertEquals(10, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void buyShares_validPurchase_createsTransactionWithBuyType()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.buyShares(request);
    assertTrue(transactionDAO.getAll().get(0).getType().equals(domain.TransactionType.BUY));
  }

  @Test void buyShares_validPurchase_setsCorrectOwnedStockQuantity()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.buyShares(request);
    assertEquals(10, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void buyShares_validPurchase_transactionTimestampIsValidInstant()
  {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.buyShares(request);

    Instant timestamp = transactionDAO.getAll().getFirst().getTimestamp();
    assertNotNull(timestamp);
    assertDoesNotThrow(() -> Instant.parse(timestamp.toString()));
  }

  @Test
  void buyShares_fee_matches_AppConfig() {
    BuySharesRequest request = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.buyShares(request);
    
    // Get the created transaction
    var transaction = transactionDAO.getAll().get(0);
    BigDecimal totalAmount = new BigDecimal("10.0").multiply(BigDecimal.valueOf(10));
    double feeRate = shared.configuration.AppConfig.getInstance().getTransactionFee();
    BigDecimal expectedFee = totalAmount.multiply(BigDecimal.valueOf(feeRate));
    BigDecimal minimumFee = shared.configuration.AppConfig.getInstance().getMinimumTransactionFee();

    if (expectedFee.compareTo(minimumFee) < 0) {
      expectedFee = minimumFee;
    }

    assertEquals(0, transaction.getFee().compareTo(expectedFee),
        "Transaction fee should match AppConfig value");
  }
}
