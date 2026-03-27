package mocks;

import domain.Stock;
import persistence.interfaces.StockDAO;

import java.util.List;

public class MockStockDAO implements StockDAO
{
  private final List<Stock> stocks = new java.util.ArrayList<>();

  public void setMockStock(Stock mockStock)
  {
    stocks.removeIf(s -> s.getSymbol().equals(mockStock.getSymbol()));
    stocks.add(mockStock);
  }

  public Stock getMockStock()
  {
    return stocks.isEmpty() ? null : stocks.get(0);
  }

  @Override public void create(Stock stock)
  {
    stocks.add(stock);
  }

  @Override public void update(Stock stock)
  {
    for (int i = 0; i < stocks.size(); i++)
    {
      if (stocks.get(i).getSymbol().equals(stock.getSymbol()))
      {
        stocks.set(i, stock);
        return;
      }
    }
  }

  @Override public void delete(String symbol)
  {
    stocks.removeIf(s -> s.getSymbol().equals(symbol));
  }

  @Override public Stock getBySymbol(String symbol)
  {
    return stocks.stream()
        .filter(s -> s.getSymbol().equals(symbol))
        .findFirst()
        .orElse(null);
  }

  @Override public List<Stock> getAll()
  {
    return List.copyOf(stocks);
  }
}
