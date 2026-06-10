package persistence.interfaces;

import domain.Stock;

import java.util.List;
import java.util.UUID;

public interface StockDAO
{
  void create(Stock stock);
  Stock getBySymbol(String symbol);
  List<Stock> getAll();
  void update(Stock stock);
  void delete(String symbol);
}
