package presentation.controllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import presentation.core.AcceptsStringArgument;
import presentation.viewModels.BuyStockViewModel;

public class BuyStockController implements AcceptsStringArgument
{
    private final BuyStockViewModel viewModel;

    @FXML private Label symbolLabel;
    @FXML private Label priceLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label estimatedCostLabel;
    @FXML private Label statusLabel;

    public BuyStockController(BuyStockViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize()
    {
        quantitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1));

        symbolLabel.textProperty().bind(viewModel.stockSymbolProperty());
        priceLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.2f", viewModel.currentPriceProperty().get()),
                        viewModel.currentPriceProperty()));
        estimatedCostLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.2f", viewModel.estimatedCostProperty().get()),
                        viewModel.estimatedCostProperty()));
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) ->
                viewModel.quantityProperty().set(newVal));
    }

    @Override
    public void setArgument(String stockSymbol)
    {
        viewModel.setStockSymbol(stockSymbol);
    }

    @FXML
    private void onBuy()
    {
        boolean success = viewModel.buy();
        if (success)
        {
            new Alert(AlertType.INFORMATION, "Purchase successful!").showAndWait();
            closeWindow();
        }
    }

    @FXML
    private void onCancel()
    {
        closeWindow();
    }

    private void closeWindow()
    {
        Stage stage = (Stage) symbolLabel.getScene().getWindow();
        stage.close();
    }
}

