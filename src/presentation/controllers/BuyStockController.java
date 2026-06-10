package presentation.controllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import presentation.core.AcceptsStringArgument;
import presentation.viewModels.BuyStockViewModel;
import shared.notifications.NotificationService;

public class BuyStockController implements AcceptsStringArgument
{
    private final BuyStockViewModel viewModel;
    private final NotificationService notificationService;

    @FXML private Label symbolLabel;
    @FXML private Label priceLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label feeLabel;
    @FXML private Label estimatedCostLabel;
    @FXML private Label statusLabel;

    public BuyStockController(BuyStockViewModel viewModel, NotificationService notificationService)
    {
        this.viewModel = viewModel;
        this.notificationService = notificationService;
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
        feeLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.2f", viewModel.feeProperty().get()),
                        viewModel.feeProperty()));
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
            notificationService.showInfo("Køb gennemført", "Purchase successful!");
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

