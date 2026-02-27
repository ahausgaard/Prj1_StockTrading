package persistence.interfaces;

import domain.StockPriceHistory;

import java.util.List;
import java.util.UUID;

public interface StockPurchaseDAO
{
  void create(StockPriceHistory stockPriceHistory);
  void delete(UUID id);
  StockPriceHistory getById(UUID id);
  List<StockPriceHistory> getAll();
}
