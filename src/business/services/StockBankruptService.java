package business.services;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

public class StockBankruptService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final UnitOfWork uow;

  public StockBankruptService(Logger logger, OwnedStockDAO ownedStockDAO, UnitOfWork uow)
  {
    this.logger = logger;
    this.ownedStockDAO = ownedStockDAO;
    this.uow = uow;
  }

  public void handleBankruptcy(String symbol)
  {
    logger.log(LoggerLevel.INFO, "Handling bankruptcy for stock: " + symbol);

    uow.begin();

    OwnedStock ownedStock = ownedStockDAO.getBySymbol(symbol);
    if (ownedStock != null)
    {
      ownedStockDAO.delete(symbol);
      logger.log(LoggerLevel.INFO,
          "Deleted OwnedStock for bankrupt symbol: " + symbol
          + " (portfolioId: " + ownedStock.getPortfolioId() + ")");
    }
    else
    {
      logger.log(LoggerLevel.INFO,
          "No OwnedStock found for bankrupt symbol: " + symbol + " — nothing to remove.");
    }

    uow.commit();
  }
}
