package business.services.trading;

import business.requests.BuySharesRequest;
import domain.OwnedStock;
import persistence.interfaces.*;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

public class BuySharesService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;

  public BuySharesService(StockDAO stockDAO, OwnedStockDAO ownedStockDAO, PortfolioDAO portfolioDAO, TransactionDAO transactionDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockDAO = stockDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.portfolioDAO = portfolioDAO;
    this.transactionDAO = transactionDAO;
    this.uow = uow;
  }

  public void buyShares(BuySharesRequest request)
  {
    logger.log(LoggerLevel.INFO, "Initiating stock purchase — Symbol: " + request.stockSymbol() + ", Quantity: " + request.quantity() + ", Portfolio ID: " + request.portfolioId());

    uow.begin();

    OwnedStock existingOwnedStock = ownedStockDAO.getBySymbol(request.stockSymbol());
    if (existingOwnedStock == null)
    {
      OwnedStock newOwnedStock = OwnedStock.createNew(request.portfolioId(), request.stockSymbol(), request.quantity());
      ownedStockDAO.create(newOwnedStock);
      logger.log(LoggerLevel.INFO, "Created new OwnedStock for symbol: " + request.stockSymbol() + " with quantity: " + request.quantity());
    }
    else
    {
      existingOwnedStock.addShares(request.quantity());
      ownedStockDAO.update(existingOwnedStock);
      logger.log(LoggerLevel.INFO, "Updated existing OwnedStock for symbol: " + request.stockSymbol() + " to new quantity: " + existingOwnedStock.getQuantity());
    }

    uow.commit();
  }
}
