package presentation.viewModels;

import business.commands.SellSharesRequest;
import business.dto.OwnedStockDTO;
import business.dto.ProfitLossDTO;
import business.services.trading.PortfolioQueryService;
import business.services.trading.SellSharesService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.UUID;

public class PortfolioViewModel
{
    private final PortfolioQueryService portfolioQueryService;
    private final SellSharesService sellSharesService;

    private final ObservableList<OwnedStockDTO> ownedStocks = FXCollections.observableArrayList();
    private final StringProperty liquidityText = new SimpleStringProperty("--");
    private final StringProperty totalValueText = new SimpleStringProperty("--");
    private final StringProperty profitLossText = new SimpleStringProperty("--");
    private final StringProperty statusMessage = new SimpleStringProperty("");

    public PortfolioViewModel(PortfolioQueryService portfolioQueryService,
            SellSharesService sellSharesService)
    {
        this.portfolioQueryService = portfolioQueryService;
        this.sellSharesService = sellSharesService;
        refresh();
    }

    public void refresh()
    {
        try
        {
            UUID portfolioId = portfolioQueryService.getDefaultPortfolioId();

            ownedStocks.setAll(portfolioQueryService.getOwnedStocks(portfolioId));

            BigDecimal balance = portfolioQueryService.getBalance(portfolioId);
            liquidityText.set(String.format("%.2f", balance));

            BigDecimal totalValue = portfolioQueryService.getTotalPortfolioValue(portfolioId);
            totalValueText.set(String.format("%.2f", totalValue));

            ProfitLossDTO pnl = portfolioQueryService.getProfitLoss(portfolioId);
            String sign = pnl.netProfitLoss().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            profitLossText.set(sign + String.format("%.2f", pnl.netProfitLoss()));
        }
        catch (Exception e)
        {
            liquidityText.set("--");
            totalValueText.set("--");
            profitLossText.set("--");
        }
    }

    public boolean sell(String stockSymbol, double quantity)
    {
        try
        {
            UUID portfolioId = portfolioQueryService.getDefaultPortfolioId();
            SellSharesRequest request = new SellSharesRequest(portfolioId, stockSymbol, quantity);
            sellSharesService.sellShares(request);
            statusMessage.set("Sold " + (int) quantity + " share(s) of " + stockSymbol + " successfully!");
            refresh();
            return true;
        }
        catch (Exception e)
        {
            statusMessage.set(e.getMessage());
            return false;
        }
    }

    public ObservableList<OwnedStockDTO> getOwnedStocks()
    {
        return ownedStocks;
    }

    public StringProperty liquidityTextProperty()
    {
        return liquidityText;
    }

    public StringProperty totalValueTextProperty()
    {
        return totalValueText;
    }

    public StringProperty profitLossTextProperty()
    {
        return profitLossText;
    }

    public StringProperty statusMessageProperty()
    {
        return statusMessage;
    }
}
