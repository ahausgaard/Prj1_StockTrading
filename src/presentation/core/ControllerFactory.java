package presentation.core;

import presentation.controllers.*;
import presentation.viewModels.*;

import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object>
{
    private final ApplicationContext context;

    public ControllerFactory(ApplicationContext context)
    {
        this.context = context;
    }

    @Override
    public Object call(Class<?> type)
    {
        if (type == MainMenuController.class)
        {
            MainMenuViewModel viewModel = new MainMenuViewModel(
                    context.getGameService());
            return new MainMenuController(viewModel);
        }
        if (type == StockPriceChartController.class)
        {
            StockPriceChartViewModel viewModel = new StockPriceChartViewModel(
                    context.getStockListenerService(),
                    context.getPortfolioQueryService());
            return new StockPriceChartController(viewModel);
        }
        if (type == BuyStockController.class)
        {
            BuyStockViewModel viewModel = new BuyStockViewModel(
                    context.getBuySharesService(),
                    context.getPortfolioQueryService());
            return new BuyStockController(viewModel);
        }
        if (type == PortfolioController.class)
        {
            PortfolioViewModel viewModel = new PortfolioViewModel(
                    context.getPortfolioQueryService(),
                    context.getSellSharesService());
            return new PortfolioController(viewModel);
        }
        if (type == NewGameSettingsController.class)
        {
            NewGameSettingsViewModel viewModel = new NewGameSettingsViewModel(context.getGameService());
            return new NewGameSettingsController(viewModel);
        }
        if (type == SellStockController.class)
        {
            SellStockViewModel viewModel = new SellStockViewModel(
                    context.getSellSharesService(),
                    context.getPortfolioQueryService());
            return new SellStockController(viewModel);
        }

        // Fallback
        try
        {
            return type.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "ControllerFactory cannot create controller: " + type.getName(), e);
        }
    }
}
