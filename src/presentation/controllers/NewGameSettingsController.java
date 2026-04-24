package presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import presentation.core.ViewManager;
import presentation.viewModels.NewGameSettingsViewModel;

public class NewGameSettingsController
{
    @FXML private ChoiceBox<String> feeStrategyChoiceBox;

    private final NewGameSettingsViewModel viewModel;

    public NewGameSettingsController(NewGameSettingsViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize()
    {
        feeStrategyChoiceBox.getItems().addAll(viewModel.getStrategyNames());
        feeStrategyChoiceBox.setValue(viewModel.getDefaultStrategyName());
    }

    @FXML
    private void onStartGame()
    {
        String selected = feeStrategyChoiceBox.getValue();
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
        Stage stage = (Stage) feeStrategyChoiceBox.getScene().getWindow();
        stage.close();
    }
}



