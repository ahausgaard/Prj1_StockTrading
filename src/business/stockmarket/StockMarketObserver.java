package business.stockmarket;

import domain.Stock;

import java.util.List;

public interface StockMarketObserver
{
  void update(List<Stock> updatedStocks);
}
