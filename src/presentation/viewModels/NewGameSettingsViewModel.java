package presentation.viewModels;

import business.services.trading.GameService;
import business.services.trading.fees.FeeStrategy;
import business.services.trading.fees.FlatFeeStrategy;
import business.services.trading.fees.PercentageFeeStrategy;
import business.services.trading.fees.PercentageMinimumFeeStrategy;

import java.util.LinkedHashMap;
import java.util.Map;

public class NewGameSettingsViewModel
{
    private final GameService gameService;
    private final Map<String, FeeStrategy> availableStrategies = new LinkedHashMap<>();

    public NewGameSettingsViewModel(GameService gameService)
    {
        this.gameService = gameService;
        availableStrategies.put("Flat Fee", new FlatFeeStrategy());
        availableStrategies.put("Percentage Fee", new PercentageFeeStrategy());
        availableStrategies.put("Percentage and minimum fee", new PercentageMinimumFeeStrategy());
    }

    public java.util.Set<String> getStrategyNames()
    {
        return availableStrategies.keySet();
    }

    public String getDefaultStrategyName()
    {
        return availableStrategies.keySet().iterator().next();
    }

    public void startGame(String selectedStrategyName)
    {
        // TODO: apply selected fee strategy before starting
        gameService.restartGame();
    }
}


