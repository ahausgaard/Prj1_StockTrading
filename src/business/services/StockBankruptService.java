package business.services;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.UnitOfWork;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.util.List;

public class StockBankruptService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final PortfolioDAO portfolioDAO;
  private final UnitOfWork uow;

  public StockBankruptService(Logger logger, OwnedStockDAO ownedStockDAO,
      PortfolioDAO portfolioDAO, UnitOfWork uow)
  {
    this.logger = logger;
    this.ownedStockDAO = ownedStockDAO;
    this.portfolioDAO = portfolioDAO;
    this.uow = uow;
  }

  public void handleBankruptcy(String symbol)
  {
    logger.log(LoggerLevel.INFO, "Handling bankruptcy for stock: " + symbol);
    uow.begin();



  }
}
