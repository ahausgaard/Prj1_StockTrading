package business.services;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;

public class BuyStockService
{
  private final Logger logger;
  private OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;

  public BuyStockService(StockDAO stockDAO, OwnedStockDAO ownedStockDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockDAO = stockDAO;
    this.uow = uow;
    this.ownedStockDAO = ownedStockDAO;
  }

  public BuyStockService create(StockDAO stockDAO, OwnedStockDAO ownedStockDAO, UnitOfWork uow)
  {
    return new BuyStockService(stockDAO, ownedStockDAO, uow);
  }


}
