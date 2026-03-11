package business.stockmarket;

import business.dto.StockUpdateEvent;

public interface StockMarketObserver
{
  void update(StockUpdateEvent event);
}
