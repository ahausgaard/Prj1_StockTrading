package presentation.viewModels;

import business.commands.SellSharesRequest;
import business.services.trading.PortfolioQueryService;
import business.services.trading.SellSharesService;
import business.services.trading.TransactionFeeCalculator;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.UUID;

public class SellStockViewModel
{
    private final SellSharesService sellSharesService;
    private final PortfolioQueryService portfolioQueryService;

    private final StringProperty stockSymbol = new SimpleStringProperty("");
    private final ObjectProperty<BigDecimal> currentPrice = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);
    private final ObjectProperty<BigDecimal> fee = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final ObjectProperty<BigDecimal> estimatedProceeds = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private final StringProperty statusMessage = new SimpleStringProperty("");

    public SellStockViewModel(SellSharesService sellSharesService,
            PortfolioQueryService portfolioQueryService)
    {
        this.sellSharesService = sellSharesService;
        this.portfolioQueryService = portfolioQueryService;

        quantity.addListener((obs, oldVal, newVal) -> recalculate());
    }

    public void setStockSymbol(String symbol)
    {
        this.stockSymbol.set(symbol);

        portfolioQueryService.getAvailableStocks().stream()
                .filter(s -> s.symbol().equals(symbol))
                .findFirst()
                .ifPresent(s -> currentPrice.set(s.currentPrice()));

        recalculate();
    }

    public boolean sell()
    {
        try
        {
            UUID portfolioId = portfolioQueryService.getDefaultPortfolioId();
            SellSharesRequest request = new SellSharesRequest(
                    portfolioId, stockSymbol.get(), quantity.get());
            sellSharesService.sellShares(request);
            statusMessage.set("Sale successful!");
            return true;
        }
        catch (Exception e)
        {
            statusMessage.set(e.getMessage());
            return false;
        }
    }

    private void recalculate()
    {
        BigDecimal total = currentPrice.get()
                .multiply(BigDecimal.valueOf(quantity.get()));
        BigDecimal calculatedFee = TransactionFeeCalculator.calculateFee(total);
        fee.set(calculatedFee);
        estimatedProceeds.set(total.subtract(calculatedFee));
    }

    // Properties

    public StringProperty stockSymbolProperty()                         { return stockSymbol; }
    public ObjectProperty<BigDecimal> currentPriceProperty()            { return currentPrice; }
    public IntegerProperty quantityProperty()                           { return quantity; }
    public ObjectProperty<BigDecimal> feeProperty()                     { return fee; }
    public ObjectProperty<BigDecimal> estimatedProceedsProperty()       { return estimatedProceeds; }
    public StringProperty statusMessageProperty()                       { return statusMessage; }
}

