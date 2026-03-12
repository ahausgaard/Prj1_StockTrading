package business.observer;

public interface StockMarketSubject
{
  void addObserver(StockMarketObserver observer);
  void removeObserver(StockMarketObserver observer);
}

