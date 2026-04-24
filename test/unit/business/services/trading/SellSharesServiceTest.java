package unit.business.services.trading;

import business.commands.BuySharesRequest;
import business.commands.SellSharesRequest;
import domain.Portfolio;
import domain.Stock;
import domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.logging.Logger;
import unit.mocks.*;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class SellSharesServiceTest
{
  private MockStockDAO stockDAO;
  private MockOwnedStockDAO ownedStockDAO;
  private MockUnitOfWork uow;
  private MockPortfolioDAO portfolioDAO;
  private MockTransactionDAO transactionDAO;
  private SellSharesService service;
  private BuySharesService buyService;
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
    stockDAO.setMockStock(Stock.createNew("NOVOB", new BigDecimal("100.0")));
    portfolioDAO.setMockPortfolio(
        Portfolio.createNew(new BigDecimal("10100.0")));
    BuySharesRequest buyRequest = new BuySharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);

    buyService = new BuySharesService(stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow);
    service = new SellSharesService(ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO);

    buyService.buyShares(buyRequest);
  }

  @Test void sellShares_oneShare_decrementsQuantityAndCreatesTransaction()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.sellShares(request);

    assertEquals(9, ownedStockDAO.getAll().get(0).getQuantity());
    long sellCount = transactionDAO.getAll().stream()
        .filter(t -> t.getType() == domain.TransactionType.SELL).count();
    assertEquals(1, sellCount);
  }

  @Test void sellShares_fractionalShare_completes()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 0.5);
    service.sellShares(request);
  }

  @Test void sellShares_allShares_completes()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.sellShares(request);
  }

  @Test void sellShares_sell_more_than_owned_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 11);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Cannot sell more shares than you own.",
        exception.getMessage());
  }

  @Test void sellShares_sell_0_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 0);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Number of shares must be greater than zero.",
        exception.getMessage());
  }

  @Test void sellShares_sell_negative_amount_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", -5);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Number of shares must be greater than zero.",
        exception.getMessage());
  }

  @Test void sellShares_sell_not_owned_stock_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "NOVOB", 1);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Cannot sell shares you do not own.",
        exception.getMessage());
  }

  @Test void sellShares_bankrupt_stock_throwsException()
  {
    stockDAO.setMockStock(Stock.createFromStorage("PNDORA", domain.StockState.BANKRUPT, new BigDecimal("10.0")));

    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);

    Exception exception = assertThrows(IllegalStateException.class,
        () -> service.sellShares(request));
    assertEquals("Cannot sell shares of a bankrupt stock: PNDORA",
        exception.getMessage());
  }

  @Test void sellShares_with_null_symbol_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), null, 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Stock symbol must not be empty.",
        exception.getMessage());
  }

  @Test void sellShares_with_empty_string_symbol_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "", 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Stock symbol must not be empty.",
        exception.getMessage());
  }

  @Test void sellShares_symbol_not_found_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "TEST", 100);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Stock with symbol TEST not found.", exception.getMessage());
  }

  //State & Behaviour
  @Test void sellShares_validSale_updatesPortfolioAndDecreasesQuantity()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 5);
    service.sellShares(request);
    assertEquals(2, uow.getCommitCount());
    assertEquals(1, ownedStockDAO.getAll().size());
    assertEquals(5, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void sellShares_validSale_createsTransactionWithSellType()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.sellShares(request);
    assertTrue(transactionDAO.getAll().get(1).getType().equals(TransactionType.SELL));
  }

  @Test void sellShares_partialSale_setsCorrectRemainingQuantity()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 9);
    service.sellShares(request);
    assertEquals(1, ownedStockDAO.getAll().get(0).getQuantity());
  }

  @Test void sellShares_validSale_transactionTimestampIsValidInstant()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.sellShares(request);

    Instant timestamp = transactionDAO.getAll().getFirst().getTimestamp();
    assertNotNull(timestamp);
    assertDoesNotThrow(() -> Instant.parse(timestamp.toString()));
  }

  @Test
  void sellShares_fee_matches_AppConfig() {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);
    service.sellShares(request);

    // Get the created transaction
    var transaction = transactionDAO.getAll().get(1);
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


