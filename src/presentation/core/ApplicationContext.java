package presentation.core;

import business.services.market.StockAlertService;
import business.services.market.StockBankruptService;
import business.services.market.StockListenerService;
import business.services.trading.*;
import business.services.trading.fees.FeeStrategy;
import business.services.trading.fees.PercentageFeeStrategy;
import business.stockmarket.StockMarket;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import adapters.CustomAlertBox;
import adapters.FileLogOutputter;
import shared.configuration.AppConfig;
import shared.logging.FileLogOutputterAdapter;
import shared.logging.Logger;
import shared.notifications.CustomAlertBoxAdapter;
import shared.notifications.NotificationService;

import java.math.BigDecimal;

public class ApplicationContext
{
    // Persistence
    private final FileUnitOfWork uow;
    private final StockDAO stockDAO;
    private final OwnedStockDAO ownedStockDAO;
    private final PortfolioDAO portfolioDAO;
    private final TransactionDAO transactionDAO;
    private final StockPriceHistoryDAO stockPriceHistoryDAO;

    // Trading services
    private final BuySharesService buySharesService;
    private final SellSharesService sellSharesService;
    private final GameService gameService;
    private final PortfolioQueryService portfolioQueryService;
    private FeeStrategy feeStrategy;

    // Market services
    private final StockListenerService stockListenerService;
    private final StockBankruptService stockBankruptService;
    private final StockAlertService stockAlertService;

    // Notifications
    private final NotificationService notificationService;

    // Presentation
    private final ControllerFactory controllerFactory;

    public ApplicationContext()
    {
        Logger.getInstance().setOutput(
            new FileLogOutputterAdapter(
                new FileLogOutputter("resources/logs/app.log", "INFO")
            )
        );

        AppConfig config = AppConfig.getInstance();
        this.feeStrategy = new PercentageFeeStrategy(config.getTransactionFee());

        // Persistence Layer
        this.uow = new FileUnitOfWork(config.getDataDirectory());
        this.stockDAO = new StockFileDAO(uow);
        this.ownedStockDAO = new OwnedStockFileDAO(uow);
        this.portfolioDAO = new PortfolioFileDAO(uow);
        this.transactionDAO = new TransactionFileDAO(uow);
        this.stockPriceHistoryDAO = new StockPriceHistoryFileDAO(uow);

        // Trading Services
        this.buySharesService = new BuySharesService(
                stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow, feeStrategy);
        this.sellSharesService = new SellSharesService(
                ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO, feeStrategy);
        this.gameService = new GameService(
                stockDAO, portfolioDAO, ownedStockDAO,
                transactionDAO, stockPriceHistoryDAO, uow);
        this.portfolioQueryService = new PortfolioQueryService(
                ownedStockDAO, portfolioDAO, stockDAO, transactionDAO);

        // Market Services
        this.stockListenerService = new StockListenerService(
                stockPriceHistoryDAO, stockDAO, uow);
        this.stockBankruptService = new StockBankruptService(
                Logger.getInstance(), ownedStockDAO, uow);
        this.stockAlertService = new StockAlertService(
                stockBankruptService,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(10));

        // Observer Registration
        StockMarket market = StockMarket.getInstance();
        market.addObserver(stockListenerService);
        market.addObserver(stockAlertService);

        // Notifications
        this.notificationService = new CustomAlertBoxAdapter(new CustomAlertBox());

        // Presentation
        this.controllerFactory = new ControllerFactory(this);
    }

    public void shutdown()
    {
        gameService.shutdown();
    }

    // Getters

    public NotificationService getNotificationService()
    {
        return notificationService;
    }

    public ControllerFactory getControllerFactory()
    {
        return controllerFactory;
    }

    public GameService getGameService()
    {
        return gameService;
    }

    public BuySharesService getBuySharesService()
    {
        return buySharesService;
    }

    public SellSharesService getSellSharesService()
    {
        return sellSharesService;
    }

    public PortfolioQueryService getPortfolioQueryService()
    {
        return portfolioQueryService;
    }

    public StockListenerService getStockListenerService()
    {
        return stockListenerService;
    }

    public StockBankruptService getStockBankruptService()
    {
        return stockBankruptService;
    }

    public StockAlertService getStockAlertService()
    {
        return stockAlertService;
    }

    public FeeStrategy getFeeStrategy()
    {
        return feeStrategy;
    }

    public void setFeeStrategy(FeeStrategy feeStrategy)
    {
        this.feeStrategy = feeStrategy;
        this.buySharesService.setFeeStrategy(feeStrategy);
        this.sellSharesService.setFeeStrategy(feeStrategy);
    }
}
