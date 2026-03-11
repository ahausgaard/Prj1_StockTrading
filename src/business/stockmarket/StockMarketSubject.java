package business.stockmarket;

public interface StockMarketSubject
{
  void addObserver(StockMarketObserver observer);
  void removeObserver(StockMarketObserver observer);
}

