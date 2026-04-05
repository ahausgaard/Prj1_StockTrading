package presentation.controllers;

import business.dto.OwnedStockDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import presentation.core.ViewManager;
import presentation.viewModels.PortfolioViewModel;

public class PortfolioController
{
    private final PortfolioViewModel viewModel;

    @FXML private TableView<OwnedStockDTO> holdingsTable;
    @FXML private TableColumn<OwnedStockDTO, String> symbolColumn;
    @FXML private TableColumn<OwnedStockDTO, String> quantityColumn;
    @FXML private TableColumn<OwnedStockDTO, String> priceColumn;
    @FXML private TableColumn<OwnedStockDTO, String> valueColumn;
    @FXML private TableColumn<OwnedStockDTO, String> stateColumn;
    @FXML private Spinner<Integer> sellQuantitySpinner;
    @FXML private Button sellButton;
    @FXML private Label liquidityLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label profitLossLabel;
    @FXML private Label statusLabel;

    public PortfolioController(PortfolioViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize()
    {
        symbolColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().symbol()));
        quantityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf((int) data.getValue().quantity())));
        priceColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().currentPrice())));
        valueColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().holdingValue())));
        stateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().state().name()));

        holdingsTable.setItems(viewModel.getOwnedStocks());

        sellQuantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1));

        liquidityLabel.textProperty().bind(viewModel.liquidityTextProperty());
        totalValueLabel.textProperty().bind(viewModel.totalValueTextProperty());
        profitLossLabel.textProperty().bind(viewModel.profitLossTextProperty());
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        sellButton.disableProperty().bind(
                holdingsTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void onSell()
    {
        OwnedStockDTO selected = holdingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (selected.state() == domain.StockState.BANKRUPT)
        {
            new Alert(AlertType.WARNING, "Cannot sell a bankrupt stock.").show();
            return;
        }

        int quantity = sellQuantitySpinner.getValue();
        viewModel.sell(selected.symbol(), quantity);
    }

    @FXML
    private void onBack()
    {
        ViewManager.showView("StockPriceChart");
    }
}
