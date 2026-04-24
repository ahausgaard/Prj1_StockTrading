package presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import presentation.core.ViewManager;
import presentation.viewModels.NewGameSettingsViewModel;

public class NewGameSettingsController
{
    @FXML private ComboBox<String> feeStrategyComboBox;

    private final NewGameSettingsViewModel viewModel;

    public NewGameSettingsController(NewGameSettingsViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize()
    {
        feeStrategyComboBox.getItems().addAll(viewModel.getStrategyNames());
        feeStrategyComboBox.setValue(viewModel.getDefaultStrategyName());
    }

    @FXML
    private void onStartGame()
    {
        String selected = feeStrategyComboBox.getValue();
        viewModel.startGame(selected);
        closeWindow();
        ViewManager.showView("StockPriceChart");
    }

    @FXML
    private void onCancel()
    {
        closeWindow();
    }

    private void closeWindow()
    {
        Stage stage = (Stage) feeStrategyComboBox.getScene().getWindow();
        stage.close();
    }
}



