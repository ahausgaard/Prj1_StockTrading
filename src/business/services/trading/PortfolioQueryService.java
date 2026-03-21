package business.services.trading;

import business.dto.BalanceSnapshotDTO;
import business.dto.OwnedStockDTO;
import business.dto.ProfitLossDTO;
import business.dto.StockDTO;
import domain.OwnedStock;
import domain.Portfolio;
import domain.Stock;
import domain.Transaction;
import domain.TransactionType;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.TransactionDAO;
import shared.configuration.AppConfig;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PortfolioQueryService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final TransactionDAO transactionDAO;

  public PortfolioQueryService(OwnedStockDAO ownedStockDAO,
      PortfolioDAO portfolioDAO, StockDAO stockDAO,
      TransactionDAO transactionDAO)
  {
    this.logger = Logger.getInstance();
    this.ownedStockDAO = ownedStockDAO;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
    this.transactionDAO = transactionDAO;
  }

  public List<StockDTO> getAvailableStocks()
  {
    logger.log(LoggerLevel.INFO, "Fetching available stocks.");
    return stockDAO.getAll().stream()
        .filter(s -> s.getCurrentState() != domain.StockState.BANKRUPT).map(
            s -> new StockDTO(s.getSymbol(), s.getCurrentPrice(),
                s.getCurrentState())).toList();
  }

  public List<OwnedStockDTO> getOwnedStocks(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Fetching owned stocks for portfolio: " + portfolioId);
    requirePortfolioExists(portfolioId);

    return ownedStockDAO.getByPortfolioId(portfolioId).stream().map(os -> {
      Stock stock = stockDAO.getBySymbol(os.getStockSymbol());
      BigDecimal holdingValue = stock.getCurrentPrice()
          .multiply(BigDecimal.valueOf(os.getQuantity()));
      return new OwnedStockDTO(os.getStockSymbol(), os.getQuantity(),
          stock.getCurrentPrice(), holdingValue, stock.getCurrentState());
    }).toList();
  }

  public BigDecimal getBalance(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Fetching balance for portfolio: " + portfolioId);
    return requirePortfolioExists(portfolioId).getCurrentBalance();
  }

  public BigDecimal getTotalPortfolioValue(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Calculating total portfolio value for: " + portfolioId);
    BigDecimal balance = requirePortfolioExists(
        portfolioId).getCurrentBalance();

    BigDecimal holdingsValue = ownedStockDAO.getByPortfolioId(portfolioId)
        .stream().map(os -> {
          BigDecimal price = stockDAO.getBySymbol(os.getStockSymbol())
              .getCurrentPrice();
          return price.multiply(BigDecimal.valueOf(os.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

    return balance.add(holdingsValue);
  }

  public List<Transaction> getTransactionHistory(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Fetching transaction history for portfolio: " + portfolioId);
    requirePortfolioExists(portfolioId);

    return transactionDAO.getByPortfolioId(portfolioId).stream()
        .sorted(Comparator.comparing(Transaction::getTimestamp)).toList();
  }


  public List<BalanceSnapshotDTO> getBalanceHistory(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Fetching balance history for portfolio: " + portfolioId);
    requirePortfolioExists(portfolioId);

    List<Transaction> sorted = transactionDAO.getByPortfolioId(portfolioId)
        .stream().sorted(Comparator.comparing(Transaction::getTimestamp))
        .toList();

    BigDecimal running = AppConfig.getInstance().getStartingBalance();
    java.util.List<BalanceSnapshotDTO> snapshots = new java.util.ArrayList<>();

    for (Transaction t : sorted)
    {
      BigDecimal change;
      if (t.getType() == TransactionType.BUY)
        change = t.getTotalAmount().add(t.getFee()).negate();
      else
        change = t.getTotalAmount().subtract(t.getFee());

      running = running.add(change);
      snapshots.add(
          new BalanceSnapshotDTO(t.getTimestamp(), running, t.getType(),
              t.getStockSymbol(), change));
    }

    return snapshots;
  }

  public ProfitLossDTO getProfitLoss(UUID portfolioId)
  {
    logger.log(LoggerLevel.INFO,
        "Calculating profit/loss for portfolio: " + portfolioId);
    requirePortfolioExists(portfolioId);

    List<Transaction> transactions = transactionDAO.getByPortfolioId(
        portfolioId);

    BigDecimal totalBuyCost = transactions.stream()
        .filter(t -> t.getType() == TransactionType.BUY)
        .map(t -> t.getTotalAmount().add(t.getFee()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalSellRevenue = transactions.stream()
        .filter(t -> t.getType() == TransactionType.SELL)
        .map(t -> t.getTotalAmount().subtract(t.getFee()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalFeesSpent = transactions.stream().map(Transaction::getFee)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal netProfitLoss = totalSellRevenue.subtract(totalBuyCost);

    return new ProfitLossDTO(totalBuyCost, totalSellRevenue, totalFeesSpent,
        netProfitLoss);
  }




  private Portfolio requirePortfolioExists(UUID portfolioId)
  {
    Portfolio portfolio = portfolioDAO.getById(portfolioId);
    if (portfolio == null)
      throw new IllegalArgumentException("Portfolio not found: " + portfolioId);
    return portfolio;
  }
}
