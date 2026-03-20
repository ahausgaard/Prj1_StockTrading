package business.services.trading;

import business.requests.BuySharesRequest;
import domain.*;
import persistence.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;

public class BuySharesService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;

  public BuySharesService(StockDAO stockDAO, OwnedStockDAO ownedStockDAO,
      PortfolioDAO portfolioDAO, TransactionDAO transactionDAO, UnitOfWork uow)
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
    logger.log(LoggerLevel.INFO,
        "Initiating stock purchase — Symbol: " + request.stockSymbol()
            + ", Quantity: " + request.quantity() + ", Portfolio ID: "
            + request.portfolioId());

    uow.begin();

    try
    {
      Stock stock = stockDAO.getBySymbol(request.stockSymbol());
      if (stock == null)
        throw new IllegalArgumentException("Stock with symbol " + request.stockSymbol() + " not found.");

      Portfolio portfolio = portfolioDAO.getById(request.portfolioId());
      if (portfolio == null)
        throw new IllegalArgumentException("Portfolio with ID " + request.portfolioId() + " not found.");

      if (request.quantity()<=0)
        throw new IllegalArgumentException("Number of shares must be greater than zero.");

      if(stock.getCurrentState() == domain.StockState.BANKRUPT)
        throw new IllegalStateException("Cannot buy shares of a bankrupt stock: " + request.stockSymbol());

      BigDecimal feeRate = BigDecimal.valueOf(AppConfig.getInstance().getTransactionFee());

      //Check balance with BigDecimal
      if(portfolio.getCurrentBalance().compareTo(stock.getCurrentPrice().multiply(new java.math.BigDecimal(request.quantity())).add(feeRate)) < 0)
        throw new IllegalStateException("Insufficient funds in portfolio to complete purchase.");



      OwnedStock existingOwnedStock = ownedStockDAO.getAll().stream().filter(ownedStock ->
          ownedStock.getPortfolioId().equals(request.portfolioId()) &&
          ownedStock.getStockSymbol().equals(request.stockSymbol()))
          .findFirst()
          .orElse(null);

      if (existingOwnedStock == null)
      {
        OwnedStock newOwnedStock = OwnedStock.createNew(request.portfolioId(),
            request.stockSymbol(), request.quantity());
        ownedStockDAO.create(newOwnedStock);
        logger.log(LoggerLevel.INFO,
            "Created new OwnedStock for symbol: " + request.stockSymbol()
                + " with quantity: " + request.quantity());
      }
      else
      {
        existingOwnedStock.addShares(request.quantity());
        ownedStockDAO.update(existingOwnedStock);
        logger.log(LoggerLevel.INFO,
            "Updated existing OwnedStock for symbol: " + request.stockSymbol()
                + " to new quantity: " + existingOwnedStock.getQuantity());
      }

      BigDecimal totalCost = stock.getCurrentPrice().multiply(new java.math.BigDecimal(request.quantity())).add(feeRate);
      portfolio.setCurrentBalance(portfolio.getCurrentBalance().subtract(totalCost));
      portfolioDAO.update(portfolio);

      transactionDAO.create(Transaction.createNew(request.portfolioId(), TransactionType.BUY, request.stockSymbol(), request.quantity(), stock.getCurrentPrice(), stock.getCurrentPrice().multiply(new java.math.BigDecimal(request.quantity())), feeRate, java.time.Instant.now()));

      uow.commit();
      logger.log(LoggerLevel.INFO,
          "Stock purchase completed successfully for symbol: " + request.stockSymbol()
              + " | Quantity: " + request.quantity() + " | Portfolio ID: "
              + request.portfolioId());
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR,
          "Error during stock purchase: " + e.getMessage());
    }
  }
}
