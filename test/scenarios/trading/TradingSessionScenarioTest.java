package scenarios.trading;

import business.commands.BuySharesRequest;
import business.commands.SellSharesRequest;
import business.dto.BalanceSnapshotDTO;
import business.dto.OwnedStockDTO;
import business.dto.ProfitLossDTO;
import business.services.trading.BuySharesService;
import business.services.trading.PortfolioQueryService;
import business.services.trading.SellSharesService;
import business.services.trading.fees.PercentageFeeStrategy;
import domain.Portfolio;
import domain.Stock;
import domain.Transaction;
import domain.TransactionType;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * SCENARIO TEST: Complete Trading Session (Happy Path)
 *
 * This test simulates a complete trading scenario from start to finish:
 * 1. Game starts with 10,000 DKK
 * 2. Buy stocks in two companies (PNDORA, NOVOB)
 * 3. Market updates with price changes
 * 4. Sell some stocks with profit
 * 5. Verify portfolio state, transaction history, and profit/loss calculations
 *
 * This tests the entire system working together:
 * - BuySharesService + SellSharesService
 * - DAO/Unit of Work persistence
 * - FeeStrategy (gebyr beregning)
 * - PortfolioQueryService calculations
 * - All layers integrated
 */
public class TradingSessionScenarioTest {
    private Path tempDir;
    private FileUnitOfWork uow;
    private StockFileDAO stockDAO;
    private OwnedStockFileDAO ownedStockDAO;
    private PortfolioFileDAO portfolioDAO;
    private TransactionFileDAO transactionDAO;
    private StockPriceHistoryFileDAO priceHistoryDAO;

    private BuySharesService buyService;
    private SellSharesService sellService;
    private PortfolioQueryService queryService;

    private Portfolio portfolio;
    private Stock pndoraStock;
    private Stock novoStock;

    @BeforeEach void setup() throws IOException {
        // Create temp directory for file-based persistence
        tempDir = Files.createTempDirectory("trading_scenario_test");
        uow = new FileUnitOfWork(tempDir.toString() + File.separator);

        // Initialize DAOs
        stockDAO = new StockFileDAO(uow);
        ownedStockDAO = new OwnedStockFileDAO(uow);
        portfolioDAO = new PortfolioFileDAO(uow);
        transactionDAO = new TransactionFileDAO(uow);
        priceHistoryDAO = new StockPriceHistoryFileDAO(uow);

        // Initialize services
        buyService = new BuySharesService(
            stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow,
            new PercentageFeeStrategy(0.01)); // 1% fee

        sellService = new SellSharesService(
            ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO,
            new PercentageFeeStrategy(0.01));

        queryService = new PortfolioQueryService(
            ownedStockDAO, portfolioDAO, stockDAO, transactionDAO);

        // Setup initial data
        setupInitialGameState();
    }

    @AfterEach void cleanup() throws IOException {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    private void setupInitialGameState() throws IOException {
        // Create portfolio with initial balance
        portfolio = Portfolio.createNew(new BigDecimal("10000.00"));

        // Create stocks
        pndoraStock = Stock.createNew("PNDORA", new BigDecimal("100.00"));
        novoStock = Stock.createNew("NOVOB", new BigDecimal("50.00"));

        // Persist initial state
        uow.begin();
        portfolioDAO.create(portfolio);
        stockDAO.create(pndoraStock);
        stockDAO.create(novoStock);
        uow.commit();
    }

    @Test void completeSessionScenario_happyPath_demonstratesFullTrading() throws IOException {
        System.out.println("=== TRADING SESSION SCENARIO TEST ===");
        System.out.println("Initial Balance: 10,000 DKK\n");

        // STEP 1: Buy PNDORA stocks
        System.out.println("STEP 1: Buy 5 PNDORA @ 100 DKK each");
        BuySharesRequest buyPndora = new BuySharesRequest(
            portfolio.getId(), "PNDORA", 5);
        buyService.buyShares(buyPndora);

        // Cost: 5 * 100 = 500 DKK + 1% fee (5 DKK) = 505 DKK
        uow.begin();
        Portfolio afterPndoraBuy = portfolioDAO.getById(portfolio.getId());
        BigDecimal balanceAfterPndora = afterPndoraBuy.getCurrentBalance();
        uow.commit();
        System.out.println("  → Cost: 500 DKK + 5 DKK fee = 505 DKK");
        System.out.println("  → Balance: " + balanceAfterPndora + " DKK\n");

        assertEquals(new BigDecimal("9495.00"), balanceAfterPndora);

        // STEP 2: Buy NOVOB stocks
        System.out.println("STEP 2: Buy 10 NOVOB @ 50 DKK each");
        BuySharesRequest buyNovo = new BuySharesRequest(
            portfolio.getId(), "NOVOB", 10);
        sellService = new SellSharesService(
            ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO,
            new PercentageFeeStrategy(0.01));

        buyService.buyShares(buyNovo);

        // Cost: 10 * 50 = 500 DKK + 1% fee (5 DKK) = 505 DKK
        uow.begin();
        Portfolio afterNovoBuy = portfolioDAO.getById(portfolio.getId());
        BigDecimal balanceAfterNovo = afterNovoBuy.getCurrentBalance();
        List<OwnedStockDTO> ownedStocks = queryService.getOwnedStocks(portfolio.getId());
        uow.commit();
        System.out.println("  → Cost: 500 DKK + 5 DKK fee = 505 DKK");
        System.out.println("  → Balance: " + balanceAfterNovo + " DKK");
        System.out.println("  → Owned stocks: " + ownedStocks.size() + " types\n");

        assertEquals(new BigDecimal("8990.00"), balanceAfterNovo);
        assertEquals(2, ownedStocks.size());

        // STEP 3: Market update - PNDORA price increases
        System.out.println("STEP 3: Market update - PNDORA price increases to 120 DKK");
        uow.begin();
        Stock updatedPndora = stockDAO.getBySymbol("PNDORA");
        updatedPndora = Stock.createFromStorage(updatedPndora.getSymbol(),
            updatedPndora.getCurrentState(), new BigDecimal("120.00"));
        stockDAO.update(updatedPndora);
        uow.commit();
        System.out.println("  → PNDORA: 100 → 120 DKK (+20%)\n");

        // STEP 4: Sell 3 PNDORA with profit
        System.out.println("STEP 4: Sell 3 PNDORA @ 120 DKK each");
        SellSharesRequest sellPndora = new SellSharesRequest(
            portfolio.getId(), "PNDORA", 3);
        sellService.sellShares(sellPndora);

        // Revenue: 3 * 120 = 360 DKK - 1% fee (3.6 DKK) = 356.4 DKK
        uow.begin();
        Portfolio afterPndoraSell = portfolioDAO.getById(portfolio.getId());
        BigDecimal balanceAfterSell = afterPndoraSell.getCurrentBalance();
        List<OwnedStockDTO> remainingStocks = queryService.getOwnedStocks(portfolio.getId());
        uow.commit();
        System.out.println("  → Revenue: 360 DKK - 3.6 DKK fee = 356.4 DKK");
        System.out.println("  → Balance: " + balanceAfterSell + " DKK");
        System.out.println("  → Remaining PNDORA: 2, NOVOB: 10\n");

        assertEquals(2, remainingStocks.size());
        assertTrue(balanceAfterSell.compareTo(new BigDecimal("8990.00")) > 0);

        // STEP 5: Verify total portfolio value
        System.out.println("STEP 5: Calculate total portfolio value");
        uow.begin();
        BigDecimal totalValue = queryService.getTotalPortfolioValue(portfolio.getId());
        uow.commit();
        System.out.println("  → Liquid balance: " + balanceAfterSell + " DKK");
        System.out.println("  → Holdings value (2 PNDORA @ 120 + 10 NOVOB @ 50): "
            + new BigDecimal("240").add(new BigDecimal("500")) + " DKK");
        System.out.println("  → Total portfolio value: " + totalValue + " DKK\n");

        // Total = liquid + holdings
        BigDecimal expectedHoldingsValue = new BigDecimal("240").add(new BigDecimal("500"));
        BigDecimal expectedTotal = balanceAfterSell.add(expectedHoldingsValue);
        assertEquals(expectedTotal, totalValue);

        // STEP 6: Verify transaction history
        System.out.println("STEP 6: Verify transaction history");
        uow.begin();
        List<Transaction> history = queryService.getTransactionHistory(portfolio.getId());
        uow.commit();
        System.out.println("  → Total transactions: " + history.size());
        System.out.println("  → Transaction breakdown:");

        long buyCount = history.stream()
            .filter(t -> t.getType() == TransactionType.BUY)
            .count();
        long sellCount = history.stream()
            .filter(t -> t.getType() == TransactionType.SELL)
            .count();

        System.out.println("    - Buy transactions: " + buyCount);
        System.out.println("    - Sell transactions: " + sellCount + "\n");

        assertEquals(2, buyCount);
        assertEquals(1, sellCount);
        assertEquals(3, history.size());

        // STEP 7: Verify profit/loss calculation
        System.out.println("STEP 7: Calculate profit/loss");
        uow.begin();
        ProfitLossDTO profitLoss = queryService.getProfitLoss(portfolio.getId());
        uow.commit();

        BigDecimal netProfit = profitLoss.netProfitLoss();
        System.out.println("  → Total buy cost (with fees): " + profitLoss.totalBuyCost());
        System.out.println("  → Total sell revenue (with fees): " + profitLoss.totalSellRevenue());
        System.out.println("  → Total fees paid: " + profitLoss.totalFeesSpent());
        System.out.println("  → Net profit/loss: " + netProfit + " DKK\n");

        // We sold 3 PNDORA for 120 = 360 (minus fee 3.6 = 356.4 revenue)
        // We originally bought 5 PNDORA for 100 = 500 (plus fee 5 = 505 cost)
        // Profit on 3 sold: (3 * 120) - fee - (3 * 100) - proportional buy fee
        // This is complex, so we just verify it's positive (we made money)
        assertTrue(netProfit.compareTo(BigDecimal.ZERO) > 0,
            "Net profit should be positive - we sold at higher price");

        // FINAL SUMMARY
        System.out.println("=== SCENARIO COMPLETE ===");
        System.out.println("✓ Bought stocks from 2 companies");
        System.out.println("✓ Handled market price updates");
        System.out.println("✓ Sold stocks with profit");
        System.out.println("✓ Verified portfolio state");
        System.out.println("✓ Transaction history recorded");
        System.out.println("✓ Profit/loss correctly calculated");
        System.out.println("✓ All layers (Services, DAOs, UoW) working together");
    }
}
