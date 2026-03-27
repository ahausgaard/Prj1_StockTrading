package business.services.trading;

import business.commands.BuySharesRequest;
import business.commands.SellSharesRequest;
import domain.Portfolio;
import domain.Stock;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.logging.Logger;

import java.math.BigDecimal;

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

  @Test void sellShares_sell_one_share_success()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.sellShares(request);

    assertEquals(9, ownedStockDAO.getAll().get(0).getQuantity());
    long sellCount = transactionDAO.getAll().stream()
        .filter(t -> t.getType() == domain.TransactionType.SELL).count();
    assertEquals(1, sellCount);
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

  @Test void sellShares_sell_not_owned_stock_throwsException()
  {
    SellSharesRequest request = new SellSharesRequest(
        portfolioDAO.getMockPortfolio().getId(), "NOVOB", 1);
    Exception exception = assertThrows(IllegalArgumentException.class,
        () -> service.sellShares(request));
    assertEquals("Cannot sell shares you do not own.",
        exception.getMessage());
  }
  }


