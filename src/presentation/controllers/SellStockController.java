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
import presentation.viewModels.SellStockViewModel;

public class SellStockController implements AcceptsStringArgument
{
    private final SellStockViewModel viewModel;

    @FXML private Label symbolLabel;
    @FXML private Label priceLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label feeLabel;
    @FXML private Label estimatedProceedsLabel;
    @FXML private Label statusLabel;

    public SellStockController(SellStockViewModel viewModel)
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
        feeLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.2f", viewModel.feeProperty().get()),
                        viewModel.feeProperty()));
        estimatedProceedsLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.2f", viewModel.estimatedProceedsProperty().get()),
                        viewModel.estimatedProceedsProperty()));
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
    private void onSell()
    {
        boolean success = viewModel.sell();
        if (success)
        {
            new Alert(AlertType.INFORMATION, "Sale successful!").showAndWait();
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

