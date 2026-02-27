package persistence.interfaces;

import domain.StockPriceHistory;

import java.util.List;
import java.util.UUID;

public interface StockPriceHistoryDAO
{
  void create(StockPriceHistory stockPriceHistory);
  void update(StockPriceHistory stockPriceHistory);
  void delete(UUID id);
  StockPriceHistory getById(UUID id);
  List<StockPriceHistory> getAll();
}
