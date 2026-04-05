package presentation.controllers;

import business.dto.StockDTO;
import domain.StockState;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import presentation.core.ViewManager;
import presentation.viewModels.StockPriceChartViewModel;

public class StockPriceChartController
{
    private final StockPriceChartViewModel viewModel;
    private String selectedSymbol;

    @FXML private TableView<StockDTO> stockTable;
    @FXML private TableColumn<StockDTO, String> symbolColumn;
    @FXML private TableColumn<StockDTO, String> priceColumn;
    @FXML private TableColumn<StockDTO, String> stateColumn;
    @FXML private Button buyButton;
    @FXML private Label liquidityLabel;

    public StockPriceChartController(StockPriceChartViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize()
    {
        symbolColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().symbol()));
        priceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().currentPrice())));
        stateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().state().name()));

        stockTable.setItems(viewModel.getStocks());

        if (liquidityLabel != null)
        {
            liquidityLabel.textProperty().bind(viewModel.liquidityTextProperty());
        }

        // Track current symbol
        stockTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) ->
                {
                    if (newVal != null) selectedSymbol = newVal.symbol();
                });

        // Re-select previous stock after tick
        viewModel.getStocks().addListener((ListChangeListener<StockDTO>) change ->
        {
            if (selectedSymbol == null) return;
            for (StockDTO stock : stockTable.getItems())
            {
                if (stock.symbol().equals(selectedSymbol))
                {
                    stockTable.getSelectionModel().select(stock);
                    return;
                }
            }
        });

        buyButton.disableProperty().bind(
                stockTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onBuy()
    {
        StockDTO selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (selected.state() == StockState.BANKRUPT)
        {
            new Alert(AlertType.WARNING, "Cannot buy a bankrupt stock.").show();
            return;
        }

        ViewManager.openWindow("BuyStock", "Buy " + selected.symbol(), selected.symbol());
    }

    @FXML
    private void onMainMenu()
    {
        ViewManager.showView("MainMenu");
    }

    @FXML
    private void onPortfolio()
    {
        ViewManager.showView("Portfolio");
    }
}

