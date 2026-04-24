package unit.business.services.trading;

import business.services.trading.GameService;
import business.stockmarket.StockMarket;
import domain.OwnedStock;
import domain.Stock;
import domain.Portfolio;
import domain.StockPriceHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.configuration.AppConfig;
import unit.mocks.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest
{
  private MockStockDAO stockDAO;
  private MockOwnedStockDAO ownedStockDAO;
  private MockUnitOfWork uow;
  private MockPortfolioDAO portfolioDAO;
  private MockTransactionDAO transactionDAO;
  private MockStockPriceHistoryDAO stockPriceHistoryDAO;
  private GameService service;
  private StockMarket stockMarket;

  @BeforeEach void setup()
  {
    stockDAO = new MockStockDAO();
    ownedStockDAO = new MockOwnedStockDAO();
    uow = new MockUnitOfWork();
    portfolioDAO = new MockPortfolioDAO();
    transactionDAO = new MockTransactionDAO();
    stockPriceHistoryDAO = new MockStockPriceHistoryDAO();

    stockMarket = StockMarket.getInstance();
    stockMarket.clearStocks();

    service = new GameService(stockDAO, portfolioDAO, ownedStockDAO,
        transactionDAO, stockPriceHistoryDAO, uow);
  }

  @AfterEach void teardown()
  {
    service.saveGame();
    stockMarket.clearStocks();
  }

  // ---- startGame ----

  //Zero and One
  @Test void startGame_noExistingStocks_seedsDefaultStocks()
  {
    service.startGame();
    List<Stock> stocks = stockDAO.getAll();
    assertEquals(5, stocks.size());
  }

  @Test void startGame_noExistingStocks_seedsExpectedSymbols()
  {
    service.startGame();
    List<String> symbols = stockDAO.getAll().stream()
        .map(Stock::getSymbol).toList();
    assertTrue(symbols.contains("AAPL"));
    assertTrue(symbols.contains("GOOGL"));
    assertTrue(symbols.contains("TSLA"));
    assertTrue(symbols.contains("AMZN"));
    assertTrue(symbols.contains("MSFT"));
  }

  @Test void startGame_noExistingStocks_createsDefaultPortfolio()
  {
    service.startGame();
    assertNotNull(portfolioDAO.getMockPortfolio());
  }

  @Test void startGame_noExistingStocks_portfolioHasStartingBalance()
  {
    service.startGame();
    BigDecimal expected = AppConfig.getInstance().getStartingBalance();
    assertEquals(0,
        portfolioDAO.getMockPortfolio().getCurrentBalance().compareTo(expected));
  }

  @Test void startGame_noExistingStocks_stocksAtResetPrice()
  {
    service.startGame();
    BigDecimal expectedPrice = AppConfig.getInstance().getStockResetValue();
    for (Stock stock : stockDAO.getAll())
    {
      assertEquals(0, stock.getCurrentPrice().compareTo(expectedPrice));
    }
  }

  @Test void startGame_noExistingStocks_addsStocksToLiveMarket()
  {
    service.startGame();
    assertEquals(5, stockMarket.getAllStocks().size());
  }

  //Boundaries
  @Test void startGame_withExistingStocks_doesNotSeedNewStocks()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));
    stockDAO.setMockStock(
        Stock.createNew("NOVOB", new BigDecimal("200.0")));

    service.startGame();
    assertEquals(2, stockDAO.getAll().size());
  }

  @Test void startGame_withExistingStocks_loadsThemIntoMarket()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));

    service.startGame();
    List<String> marketSymbols = stockMarket.getAllStocks().stream()
        .map(Stock::getSymbol).toList();
    assertTrue(marketSymbols.contains("PNDORA"));
    assertEquals(1, marketSymbols.size());
  }

  @Test void startGame_withExistingPortfolio_doesNotCreateAnother()
  {
    portfolioDAO.setMockPortfolio(
        Portfolio.createNew(new BigDecimal("5000.0")));
    UUID existingId = portfolioDAO.getMockPortfolio().getId();

    service.startGame();
    assertEquals(existingId, portfolioDAO.getMockPortfolio().getId());
  }

  //State & Behaviour
  @Test void startGame_noExistingStocks_commitsOnce()
  {
    service.startGame();
    assertEquals(1, uow.getCommitCount());
  }

  @Test void startGame_withExistingStocks_commitsOnce()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));
    service.startGame();
    assertEquals(1, uow.getCommitCount());
  }

  // ---- saveGame ----

  //Zero and One
  @Test void saveGame_afterStart_commitsOnce()
  {
    service.startGame();
    int commitsBefore = uow.getCommitCount();
    service.saveGame();
    assertEquals(commitsBefore + 1, uow.getCommitCount());
  }

  @Test void saveGame_persistsMarketStocksToDAO()
  {
    service.startGame();
    service.saveGame();

    List<Stock> persisted = stockDAO.getAll();
    List<String> marketSymbols = stockMarket.getAllStocks().stream()
        .map(Stock::getSymbol).toList();

    for (String symbol : marketSymbols)
    {
      assertTrue(persisted.stream()
          .anyMatch(s -> s.getSymbol().equals(symbol)));
    }
  }

  //Boundaries
  @Test void saveGame_noMarketStocks_commitsWithoutCreating()
  {
    // No startGame called — market is empty
    service.saveGame();
    assertEquals(1, uow.getCommitCount());
    assertTrue(stockDAO.getAll().isEmpty());
  }

  //State & Behaviour
  @Test void saveGame_updatesExistingStockInDAO()
  {
    stockDAO.setMockStock(
        Stock.createNew("AAPL", new BigDecimal("100.0")));

    // Put a stock in the live market as well
    stockMarket.addExistingStock(
        Stock.createFromStorage("AAPL", domain.StockState.GROWING,
            new BigDecimal("200.0")));

    service.saveGame();

    Stock persisted = stockDAO.getBySymbol("AAPL");
    assertNotNull(persisted);
    assertEquals(0,
        persisted.getCurrentPrice().compareTo(new BigDecimal("200.0")));
  }

  @Test void saveGame_createsNewStockInDAOIfNotPresent()
  {
    stockMarket.addExistingStock(
        Stock.createNew("NEWSTOCK", new BigDecimal("300.0")));

    service.saveGame();

    Stock persisted = stockDAO.getBySymbol("NEWSTOCK");
    assertNotNull(persisted);
  }

  // ---- loadGame ----

  //Zero and One
  @Test void loadGame_withSavedStocks_loadsThemIntoMarket()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));
    stockDAO.setMockStock(
        Stock.createNew("NOVOB", new BigDecimal("200.0")));

    service.loadGame();

    List<String> marketSymbols = stockMarket.getAllStocks().stream()
        .map(Stock::getSymbol).toList();
    assertEquals(2, marketSymbols.size());
    assertTrue(marketSymbols.contains("PNDORA"));
    assertTrue(marketSymbols.contains("NOVOB"));
  }

  @Test void loadGame_withSavedStocks_commitsOnce()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));
    service.loadGame();
    assertEquals(1, uow.getCommitCount());
  }

  //Boundaries
  @Test void loadGame_noSavedStocks_fallsBackToStartGame()
  {
    service.loadGame();

    // startGame seeds 5 default stocks
    assertEquals(5, stockDAO.getAll().size());
    assertEquals(5, stockMarket.getAllStocks().size());
  }

  //State & Behaviour
  @Test void loadGame_clearsMarketBeforeLoading()
  {
    // Manually add a stock to the market
    stockMarket.addExistingStock(
        Stock.createNew("OLD", new BigDecimal("1.0")));

    stockDAO.setMockStock(
        Stock.createNew("NEW", new BigDecimal("2.0")));

    service.loadGame();

    List<String> marketSymbols = stockMarket.getAllStocks().stream()
        .map(Stock::getSymbol).toList();
    assertFalse(marketSymbols.contains("OLD"));
    assertTrue(marketSymbols.contains("NEW"));
  }

  // ---- restartGame ----

  //Zero and One
  @Test void restartGame_clearsExistingStocksFromDAO()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));
    stockDAO.setMockStock(
        Stock.createNew("NOVOB", new BigDecimal("200.0")));

    service.restartGame();

    // After restart, only the 5 freshly seeded defaults should remain
    List<String> symbols = stockDAO.getAll().stream()
        .map(Stock::getSymbol).toList();
    assertFalse(symbols.contains("PNDORA"));
    assertFalse(symbols.contains("NOVOB"));
    assertEquals(5, symbols.size());
  }

  @Test void restartGame_clearsOwnedStocks()
  {
    ownedStockDAO.create(
        OwnedStock.createNew(UUID.randomUUID(), "PNDORA", 10));

    service.restartGame();
    assertTrue(ownedStockDAO.getAll().isEmpty());
  }

  @Test void restartGame_clearsTransactions()
  {
    transactionDAO.create(domain.Transaction.createNew(
        UUID.randomUUID(), domain.TransactionType.BUY, "PNDORA",
        10, new BigDecimal("100.0"), new BigDecimal("1000.0"),
        new BigDecimal("10.0"), Instant.now()));

    service.restartGame();
    assertTrue(transactionDAO.getAll().isEmpty());
  }

  @Test void restartGame_clearsPriceHistories()
  {
    stockPriceHistoryDAO.create(
        StockPriceHistory.createNew("PNDORA", new BigDecimal("100.0"),
            Instant.now()));

    service.restartGame();
    assertTrue(stockPriceHistoryDAO.getAll().isEmpty());
  }

  @Test void restartGame_clearsPortfolio()
  {
    portfolioDAO.setMockPortfolio(
        Portfolio.createNew(new BigDecimal("5000.0")));

    service.restartGame();

    // After restart, the old portfolio is replaced by a freshly seeded one
    BigDecimal expected = AppConfig.getInstance().getStartingBalance();
    assertEquals(0,
        portfolioDAO.getMockPortfolio().getCurrentBalance().compareTo(expected));
  }

  //State & Behaviour
  @Test void restartGame_seedsFreshDefaultStocks()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));

    service.restartGame();

    List<String> symbols = stockDAO.getAll().stream()
        .map(Stock::getSymbol).toList();
    assertTrue(symbols.contains("AAPL"));
    assertTrue(symbols.contains("GOOGL"));
    assertTrue(symbols.contains("TSLA"));
    assertTrue(symbols.contains("AMZN"));
    assertTrue(symbols.contains("MSFT"));
  }

  @Test void restartGame_createsNewPortfolioWithStartingBalance()
  {
    service.restartGame();

    assertNotNull(portfolioDAO.getMockPortfolio());
    BigDecimal expected = AppConfig.getInstance().getStartingBalance();
    assertEquals(0,
        portfolioDAO.getMockPortfolio().getCurrentBalance().compareTo(expected));
  }

  @Test void restartGame_marketHasFiveStocksAfterRestart()
  {
    stockDAO.setMockStock(
        Stock.createNew("PNDORA", new BigDecimal("150.0")));

    service.restartGame();
    assertEquals(5, stockMarket.getAllStocks().size());
  }
}
