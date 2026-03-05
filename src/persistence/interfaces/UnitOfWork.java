package persistence.interfaces;

import domain.*;

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
  List<OwnedStock> getOwnedStocks();
}
