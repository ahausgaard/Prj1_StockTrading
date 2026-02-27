package persistence.interfaces;

import domain.Stock;

import java.util.List;
import java.util.UUID;

public interface StockDAO
{
  void create(Stock stock);
  void update(Stock stock);
  void delete(String symbol);
  Stock getBySymbol(String symbol);
  List<Stock> getAll();
}
