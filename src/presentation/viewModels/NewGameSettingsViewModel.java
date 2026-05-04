package presentation.viewModels;

import business.services.trading.fees.FeeStrategy;
import business.services.trading.fees.FlatFeeStrategy;
import business.services.trading.fees.PercentageFeeStrategy;
import business.services.trading.fees.PercentageMinimumFeeStrategy;
import presentation.core.ApplicationContext;
import shared.configuration.AppConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class NewGameSettingsViewModel
{
    private final ApplicationContext context;
    private final Map<String, FeeStrategy> availableStrategies = new LinkedHashMap<>();

    public NewGameSettingsViewModel(ApplicationContext context)
    {
        this.context = context;
        AppConfig config = AppConfig.getInstance();
        availableStrategies.put("Flat Fee", new FlatFeeStrategy(config.getMinimumTransactionFee()));
        availableStrategies.put("Percentage Fee", new PercentageFeeStrategy(config.getTransactionFee()));
        availableStrategies.put("Percentage and minimum fee", new PercentageMinimumFeeStrategy(
                config.getTransactionFee(),
                config.getMinimumTransactionFee()));
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
        FeeStrategy selected = availableStrategies.get(selectedStrategyName);
        context.setFeeStrategy(selected);
        context.getGameService().restartGame();
    }
}
