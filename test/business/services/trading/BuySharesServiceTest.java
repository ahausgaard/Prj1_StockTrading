package business.services.trading;

import business.commands.BuySharesRequest;
import domain.Portfolio;
import domain.Stock;
import mocks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


public class BuySharesServiceTest
{
  private MockStockDAO stockDAO;
  private MockOwnedStockDAO ownedStockDAO;
  private MockUnitOfWork uow;
  private MockPortfolioDAO portfolioDAO;
  private MockTransactionDAO transactionDAO;
  private BuySharesService service;

  @BeforeEach void setup()
  {
    stockDAO = new MockStockDAO();
    ownedStockDAO = new MockOwnedStockDAO();
    uow = new MockUnitOfWork();
    portfolioDAO = new MockPortfolioDAO();
    transactionDAO = new MockTransactionDAO();

    stockDAO.setMockStock(Stock.createNew("PNDORA", new BigDecimal("150.0")));
    portfolioDAO.setMockPortfolio(Portfolio.createNew(new BigDecimal("10000.0")));

    service = new BuySharesService(stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow);

  }

  @Test void buyShares()
  {
    BuySharesRequest request = new BuySharesRequest(portfolioDAO.getMockPortfolio().getId(), "PNDORA", 1);
    service.buyShares(request);
    assertEquals(1,uow.getCommitCount());
  }
}
