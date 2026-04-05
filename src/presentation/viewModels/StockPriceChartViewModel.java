package presentation.viewModels;

import business.dto.StockDTO;
import business.services.market.StockListenerService;
import business.services.trading.PortfolioQueryService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.UUID;

public class StockPriceChartViewModel
{
    private final StockListenerService stockListenerService;
    private final PortfolioQueryService portfolioQueryService;
    private final ObservableList<StockDTO> stocks = FXCollections.observableArrayList();
    private final StringProperty liquidityText = new SimpleStringProperty("--");

    public StockPriceChartViewModel(StockListenerService stockListenerService,
            PortfolioQueryService portfolioQueryService)
    {
        this.stockListenerService = stockListenerService;
        this.portfolioQueryService = portfolioQueryService;

        stocks.setAll(stockListenerService.getStocks());
        refreshLiquidity();

        stockListenerService.setOnStocksUpdated(() ->
                Platform.runLater(() ->
                {
                    stocks.setAll(stockListenerService.getStocks());
                    refreshLiquidity();
                }));
    }

    private void refreshLiquidity()
    {
        try
        {
            UUID portfolioId = portfolioQueryService.getDefaultPortfolioId();
            BigDecimal balance = portfolioQueryService.getBalance(portfolioId);
            liquidityText.set(String.format("%.2f", balance));
        }
        catch (Exception e)
        {
            liquidityText.set("--");
        }
    }

    public ObservableList<StockDTO> getStocks()
    {
        return stocks;
    }

    public StringProperty liquidityTextProperty()
    {
        return liquidityText;
    }
}
