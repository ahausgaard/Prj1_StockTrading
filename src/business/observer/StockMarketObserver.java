package business.observer;

public interface StockMarketObserver
{
  void update(StockUpdateEvent event);
}
