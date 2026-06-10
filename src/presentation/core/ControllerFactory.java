package presentation.core;

import presentation.controllers.*;
import presentation.viewModels.*;
import shared.notifications.NotificationService;

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
            return new StockPriceChartController(viewModel, context.getNotificationService());
        }
        if (type == BuyStockController.class)
        {
            BuyStockViewModel viewModel = new BuyStockViewModel(
                    context.getBuySharesService(),
                    context.getPortfolioQueryService(),
                    context.getFeeStrategy());
            return new BuyStockController(viewModel, context.getNotificationService());
        }
        if (type == PortfolioController.class)
        {
            PortfolioViewModel viewModel = new PortfolioViewModel(
                    context.getPortfolioQueryService(),
                    context.getSellSharesService());
            return new PortfolioController(viewModel, context.getNotificationService());
        }
        if (type == NewGameSettingsController.class)
        {
            NewGameSettingsViewModel viewModel = new NewGameSettingsViewModel(context);
            return new NewGameSettingsController(viewModel);
        }
        if (type == SellStockController.class)
        {
            SellStockViewModel viewModel = new SellStockViewModel(
                    context.getSellSharesService(),
                    context.getPortfolioQueryService(),
                    context.getFeeStrategy());
            return new SellStockController(viewModel, context.getNotificationService());
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
