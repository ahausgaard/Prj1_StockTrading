package presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import presentation.core.ViewManager;
import presentation.viewModels.MainMenuViewModel;

public class MainMenuController
{
    private final MainMenuViewModel viewModel;

    @FXML private Button newGameButton;
    @FXML private Button loadGameButton;
    @FXML private Button exitButton;

    public MainMenuController(MainMenuViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    @FXML
    private void onNewGame()
    {
        ViewManager.openModalWindow("NewGameSettings", "New Game");
    }

    @FXML
    private void onLoadGame()
    {
        viewModel.loadGame();
        ViewManager.showView("StockPriceChart");
    }

    @FXML
    private void onExit()
    {
        viewModel.exit();
    }
}
