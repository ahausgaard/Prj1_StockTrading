package unit.business.services.trading;

import business.commands.BuySharesRequest;
import domain.Portfolio;
import domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.logging.Logger;
import unit.mocks.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioQueryServiceTest {
    private PortfolioQueryService queryService;
    private MockStockDAO stockDAO;
    private MockOwnedStockDAO ownedStockDAO;
    private MockUnitOfWork uow;
    private MockPortfolioDAO portfolioDAO;
    private MockTransactionDAO transactionDAO;
    private SellSharesService sellService;
    private BuySharesService buyService;
    private Logger logger;

    @BeforeEach void setup() {
        stockDAO = new MockStockDAO();
        ownedStockDAO = new MockOwnedStockDAO();
        uow = new MockUnitOfWork();
        portfolioDAO = new MockPortfolioDAO();
        transactionDAO = new MockTransactionDAO();
        this.logger = Logger.getInstance();

        stockDAO.setMockStock(Stock.createNew("PNDORA", new BigDecimal("10.0")));
        stockDAO.setMockStock(Stock.createNew("NOVOB", new BigDecimal("100.0")));
        portfolioDAO.setMockPortfolio(Portfolio.createNew(new BigDecimal("10100.0")));
        BuySharesRequest buyRequest = new BuySharesRequest(portfolioDAO.getMockPortfolio().getId(), "PNDORA", 10);

        buyService = new BuySharesService(stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow);
        sellService = new SellSharesService(ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO);

        buyService.buyShares(buyRequest);
        queryService = new PortfolioQueryService(ownedStockDAO, portfolioDAO, stockDAO, transactionDAO);
    }

    @Test void pqs_GetAvailableStocks_excludesBankrupt() {
        Stock bankrupt = Stock.createFromStorage("BANKRUPT", domain.StockState.BANKRUPT, new BigDecimal("1.0"));
        stockDAO.setMockStock(bankrupt);
        var stocks = queryService.getAvailableStocks();
        assertTrue(stocks.stream().anyMatch(s -> s.symbol().equals("PNDORA")));
        assertTrue(stocks.stream().anyMatch(s -> s.symbol().equals("NOVOB")));
        assertFalse(stocks.stream().anyMatch(s -> s.symbol().equals("BANKRUPT")));
    }

    @Test void pqs_GetOwnedStocks_reflectsPurchase() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        var owned = queryService.getOwnedStocks(portfolioId);
        assertEquals(1, owned.size());
        assertEquals("PNDORA", owned.get(0).symbol());
        assertEquals(10, owned.get(0).quantity());
    }

    @Test void pqs_GetBalance_afterBuy() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        BigDecimal balance = queryService.getBalance(portfolioId);

        assertTrue(balance.compareTo(new BigDecimal("10100.0")) < 0);
    }

    @Test void pqs_GetTotalPortfolioValue() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        BigDecimal value = queryService.getTotalPortfolioValue(portfolioId);

        BigDecimal expectedValue = queryService.getBalance(portfolioId).add(new BigDecimal("100.00"));
        assertEquals(expectedValue, value);
    }

    @Test void pqs_GetTransactionHistory_includesBuy() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        var history = queryService.getTransactionHistory(portfolioId);
        assertEquals(1, history.size());
        assertEquals(domain.TransactionType.BUY, history.get(0).getType());
        assertEquals("PNDORA", history.get(0).getStockSymbol());
    }

    @Test void pqs_GetBalanceHistory_reflectsTransactions() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        var history = queryService.getBalanceHistory(portfolioId);
        assertFalse(history.isEmpty());
        // Calculate expected balance after buy (initial - shares*price - fee)
        BigDecimal initial = new BigDecimal("10100.0");
        BigDecimal sharesCost = new BigDecimal("100.0");
        BigDecimal fee = history.get(0).changeAmount().negate().subtract(sharesCost); // fee is the extra negative change
        BigDecimal expected = initial.subtract(sharesCost).subtract(fee);
        assertEquals(expected, history.get(history.size()-1).balanceAfter());
    }

    @Test void pqs_GetProfitLoss_afterBuy() {
        var portfolioId = portfolioDAO.getMockPortfolio().getId();
        var pl = queryService.getProfitLoss(portfolioId);

        assertEquals(BigDecimal.ZERO, pl.totalSellRevenue());
        assertTrue(pl.totalBuyCost().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(pl.totalFeesSpent().compareTo(BigDecimal.ZERO) >= 0);
        
        assertTrue(pl.netProfitLoss().compareTo(BigDecimal.ZERO) < 0);
    }
}
