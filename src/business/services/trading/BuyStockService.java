package business.services.trading;

import domain.Portfolio;
import domain.Transaction;
import persistence.interfaces.*;
import shared.logging.Logger;

public class BuyStockService
{
  private final Logger logger;
  private OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;

  public BuyStockService(StockDAO stockDAO, OwnedStockDAO ownedStockDAO, PortfolioDAO portfolioDAO, TransactionDAO transactionDAO, UnitOfWork uow)
  {
    this.logger = Logger.getInstance();
    this.stockDAO = stockDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.portfolioDAO = portfolioDAO;
    this.transactionDAO = transactionDAO;
    this.uow = uow;
  }




}
