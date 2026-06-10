package unit.presentation.viewModels;

import business.services.trading.BuySharesService;
import business.services.trading.PortfolioQueryService;
import business.services.trading.fees.PercentageFeeStrategy;
import domain.Portfolio;
import domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import presentation.viewModels.BuyStockViewModel;
import unit.mocks.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BuyStockViewModelTest
{
    private BuyStockViewModel viewModel;

    @BeforeEach
    void setup()
    {
        MockStockDAO stockDAO = new MockStockDAO();
        MockOwnedStockDAO ownedStockDAO = new MockOwnedStockDAO();
        MockPortfolioDAO portfolioDAO = new MockPortfolioDAO();
        MockTransactionDAO transactionDAO = new MockTransactionDAO();
        MockUnitOfWork uow = new MockUnitOfWork();

        stockDAO.setMockStock(Stock.createNew("PNDORA", new BigDecimal("100.00")));
        portfolioDAO.setMockPortfolio(Portfolio.createNew(new BigDecimal("10000.00")));

        BuySharesService buyService = new BuySharesService(
                stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow,
                new PercentageFeeStrategy(0.01));

        PortfolioQueryService queryService = new PortfolioQueryService(
                ownedStockDAO, portfolioDAO, stockDAO, transactionDAO);

        viewModel = new BuyStockViewModel(buyService, queryService,
                new PercentageFeeStrategy(0.01));
    }

    @Test
    void buy_validPurchase_returnsTrueAndSetsSuccessMessage()
    {
        viewModel.setStockSymbol("PNDORA");
        viewModel.quantityProperty().set(5);

        boolean result = viewModel.buy();

        assertTrue(result);
        assertEquals("Purchase successful!", viewModel.statusMessageProperty().get());
    }

    @Test
    void buy_insufficientFunds_returnsFalseAndSetsErrorMessage()
    {
        viewModel.setStockSymbol("PNDORA");
        viewModel.quantityProperty().set(99999);

        boolean result = viewModel.buy();

        assertFalse(result);
        assertFalse(viewModel.statusMessageProperty().get().isEmpty());
    }

    @Test
    void quantityChange_updatesEstimatedCostReactively()
    {
        // Ingen service-kald — kun reaktiv binding via JavaFX properties
        viewModel.setStockSymbol("PNDORA"); // sætter currentPrice til 100.00
        viewModel.quantityProperty().set(5);

        // 5 × 100 = 500 + 1% gebyr (5) = 505
        assertEquals(0,
                viewModel.estimatedCostProperty().get().compareTo(new BigDecimal("505")),
                "Estimated cost should be 505.00 (500 + 1% fee)");
    }
}
