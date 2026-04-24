package presentation.core;

import business.services.market.StockAlertService;
import business.services.market.StockBankruptService;
import business.services.market.StockListenerService;
import business.services.trading.*;
import business.stockmarket.StockMarket;
import persistence.fileImplementation.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.Logger;

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

    // Market services
    private final StockListenerService stockListenerService;
    private final StockBankruptService stockBankruptService;
    private final StockAlertService stockAlertService;

    // Presentation
    private final ControllerFactory controllerFactory;

    public ApplicationContext()
    {
        AppConfig config = AppConfig.getInstance();

        // Persistence Layer
        this.uow = new FileUnitOfWork(config.getDataDirectory());
        this.stockDAO = new StockFileDAO(uow);
        this.ownedStockDAO = new OwnedStockFileDAO(uow);
        this.portfolioDAO = new PortfolioFileDAO(uow);
        this.transactionDAO = new TransactionFileDAO(uow);
        this.stockPriceHistoryDAO = new StockPriceHistoryFileDAO(uow);

        // Trading Services
        this.buySharesService = new BuySharesService(
                stockDAO, ownedStockDAO, portfolioDAO, transactionDAO, uow);
        this.sellSharesService = new SellSharesService(
                ownedStockDAO, stockDAO, uow, transactionDAO, portfolioDAO);
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

        // Presentation
        this.controllerFactory = new ControllerFactory(this);
    }

    // Getters

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
}
