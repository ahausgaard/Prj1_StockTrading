package business.services.trading;

import business.commands.SellSharesRequest;
import domain.*;
import persistence.interfaces.*;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.math.BigDecimal;

public class SellSharesService
{
  private final Logger logger;
  private final OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final UnitOfWork uow;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;

  public SellSharesService(Logger logger, OwnedStockDAO ownedStockDAO,
      StockDAO stockDAO, UnitOfWork uow, TransactionDAO transactionDAO,
      PortfolioDAO portfolioDAO)
  {
    this.logger = logger;
    this.ownedStockDAO = ownedStockDAO;
    this.stockDAO = stockDAO;
    this.uow = uow;
    this.transactionDAO = transactionDAO;
    this.portfolioDAO = portfolioDAO;
  }

  public void sellShares(SellSharesRequest request)
  {
    logger.log(LoggerLevel.INFO,
        "Initiating stock sale — Symbol: " + request.stockSymbol()
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
        throw new IllegalStateException("Cannot sell shares of a bankrupt stock: " + request.stockSymbol());

      OwnedStock existingOwnedStock = ownedStockDAO.getAll().stream().filter(ownedStock ->
              ownedStock.getPortfolioId().equals(request.portfolioId()) &&
              ownedStock.getStockSymbol().equals(request.stockSymbol()))
          .findFirst()
          .orElse(null);

      if (existingOwnedStock == null)
        throw new IllegalArgumentException("Cannot sell shares you do not own.");

      if(request.quantity() > existingOwnedStock.getQuantity())
        throw new IllegalArgumentException("Cannot sell more shares than you own");

      existingOwnedStock.removeShares(request.quantity());
      //Delete if quantity reaches zero
      if (existingOwnedStock.getQuantity() == 0) {
        ownedStockDAO.delete(existingOwnedStock.getStockSymbol());
        logger.log(LoggerLevel.INFO, "Deleted OwnedStock after sale: " + existingOwnedStock.getStockSymbol() + " (quantity reached zero)");
      } else {
        ownedStockDAO.update(existingOwnedStock);
        logger.log(LoggerLevel.INFO, "Updated OwnedStock after sale: " + existingOwnedStock.getStockSymbol() + ", New Quantity: " + existingOwnedStock.getQuantity());
      }

      BigDecimal totalSaleValue = stock.getCurrentPrice().multiply(new BigDecimal(request.quantity()));
      BigDecimal fee = TransactionFeeCalculator.calculateFee(totalSaleValue);
      BigDecimal netSaleValue = totalSaleValue.subtract(fee);
      portfolio.setCurrentBalance(portfolio.getCurrentBalance().add(netSaleValue));
      portfolioDAO.update(portfolio);

      transactionDAO.create(Transaction.createNew(
        request.portfolioId(),
        TransactionType.SELL,
        request.stockSymbol(),
        request.quantity(),
        stock.getCurrentPrice(),
        totalSaleValue,
        fee,
        java.time.Instant.now()
      ));

      uow.commit();
      logger.log(LoggerLevel.INFO, "Stock sale completed successfully for symbol: " + request.stockSymbol() + ", Quantity: " + request.quantity());
    }
    catch (Exception e)
    {
      uow.rollback();
      logger.log(LoggerLevel.ERROR, "Error during stock sale: " + e.getMessage());
    }

  }
}
