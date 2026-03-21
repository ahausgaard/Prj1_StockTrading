package business.services.trading;

import business.observer.OwnedStockDTO;
import business.observer.StockDTO;
import domain.OwnedStock;
import domain.Portfolio;
import domain.Stock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.StockDAO;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PortfolioQueryService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;

  public PortfolioQueryService(OwnedStockDAO ownedStockDAO,
      PortfolioDAO portfolioDAO, StockDAO stockDAO)
  {
    this.logger = Logger.getInstance();
    this.ownedStockDAO = ownedStockDAO;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
  }


  public List<StockDTO> getAvailableStocks()
  {
    logger.log(LoggerLevel.INFO, "Fetching available stocks.");
    return stockDAO.getAll().stream()
        .filter(s -> s.getCurrentState() != domain.StockState.BANKRUPT)
        .map(s -> new StockDTO(s.getSymbol(), s.getCurrentPrice(), s.getCurrentState()))
        .toList();
  }

  public List<OwnedStockDTO> getOwnedStocks(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO, "Fetching owned stocks for portfolio: " + portfolioId);
    requirePortfolioExists(portfolioId);

    return ownedStockDAO.getByPortfolioId(portfolioId).stream()
        .map(os -> {
          Stock stock = stockDAO.getBySymbol(os.getStockSymbol());
          BigDecimal holdingValue = stock.getCurrentPrice()
              .multiply(BigDecimal.valueOf(os.getQuantity()));
          return new OwnedStockDTO(
              os.getStockSymbol(),
              os.getQuantity(),
              stock.getCurrentPrice(),
              holdingValue,
              stock.getCurrentState());
        })
        .toList();
  }

  public BigDecimal getBalance(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO, "Fetching balance for portfolio: " + portfolioId);
    return requirePortfolioExists(portfolioId).getCurrentBalance();
  }


  public BigDecimal getTotalPortfolioValue(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO, "Calculating total portfolio value for: " + portfolioId);
    BigDecimal balance = requirePortfolioExists(portfolioId).getCurrentBalance();

    BigDecimal holdingsValue = ownedStockDAO.getByPortfolioId(portfolioId).stream()
        .map(os -> {
          BigDecimal price = stockDAO.getBySymbol(os.getStockSymbol()).getCurrentPrice();
          return price.multiply(BigDecimal.valueOf(os.getQuantity()));
        })
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return balance.add(holdingsValue);
  }


  private Portfolio requirePortfolioExists(UUID portfolioId)
  {
    Portfolio portfolio = portfolioDAO.getById(portfolioId);
    if (portfolio == null)
      throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
    return portfolio;
  }
}
