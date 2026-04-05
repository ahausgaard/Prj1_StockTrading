package presentation.viewModels;

import business.services.trading.GameService;
import javafx.application.Platform;

public class MainMenuViewModel
{
    private final GameService gameService;

    public MainMenuViewModel(GameService gameService)
    {
        this.gameService = gameService;
    }

    public void newGame()
    {
        gameService.restartGame();
    }

    public void loadGame()
    {
        gameService.loadGame();
    }

    public void exit()
    {
        Platform.exit();
    }
}
