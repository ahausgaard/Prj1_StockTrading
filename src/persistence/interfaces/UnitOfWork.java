package persistence.interfaces;

import domain.Portfolio;
import domain.Stock;
import domain.StockPriceHistory;
import domain.Transaction;

import java.util.List;

public interface UnitOfWork
{
  void begin();
  void commit();
  void rollback();
  List<Portfolio> getPortfolios();
  List<Stock> getStocks();
  List<StockPriceHistory> getStockPriceHistories();
  List<Transaction> getTransactions();

}
