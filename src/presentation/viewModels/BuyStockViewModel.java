package presentation.viewModels;

import business.commands.BuySharesRequest;
import business.services.trading.BuySharesService;
import business.services.trading.PortfolioQueryService;
import business.services.trading.TransactionFeeCalculator;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.UUID;

public class BuyStockViewModel
{
    private final BuySharesService buySharesService;
    private final PortfolioQueryService portfolioQueryService;

    private final StringProperty stockSymbol = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> currentPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final ObjectProperty<BigDecimal> estimatedCost = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final StringProperty statusMessage = new SimpleStringProperty("");

    public BuyStockViewModel(BuySharesService buySharesService,
            PortfolioQueryService portfolioQueryService)
    {
        this.buySharesService = buySharesService;
        this.portfolioQueryService = portfolioQueryService;

        quantity.addListener((obs, oldVal, newVal) -> recalculateEstimatedCost());
    }

    public void setStockSymbol(String symbol)
    {
        this.stockSymbol.set(symbol);

        portfolioQueryService.getAvailableStocks().stream()
                .filter(s -> s.symbol().equals(symbol))
                .findFirst()
                .ifPresent(s -> currentPrice.set(s.currentPrice()));

        recalculateEstimatedCost();
    }

    public boolean buy()
    {
        try
        {
            UUID portfolioId = portfolioQueryService.getDefaultPortfolioId();
            BuySharesRequest request = new BuySharesRequest(
                    portfolioId, stockSymbol.get(), quantity.get());
            buySharesService.buyShares(request);
            statusMessage.set("Purchase successful!");
            return true;
        }
        catch (Exception e)
        {
            statusMessage.set(e.getMessage());
            return false;
        }
    }

    private void recalculateEstimatedCost()
    {
        BigDecimal total = currentPrice.get()
                .multiply(BigDecimal.valueOf(quantity.get()));
        BigDecimal fee = TransactionFeeCalculator.calculateFee(total);
        estimatedCost.set(total.add(fee));
    }

    //Properties

    public StringProperty stockSymbolProperty()       { return stockSymbol; }
    public ObjectProperty<BigDecimal> currentPriceProperty() { return currentPrice; }
    public IntegerProperty quantityProperty()          { return quantity; }
    public ObjectProperty<BigDecimal> estimatedCostProperty() { return estimatedCost; }
    public StringProperty statusMessageProperty()      { return statusMessage; }
}

