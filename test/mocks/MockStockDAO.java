package mocks;

import domain.Stock;
import persistence.interfaces.StockDAO;

import java.util.List;

public class MockStockDAO implements StockDAO
{
  private Stock mockStock;

  public void setMockStock(Stock mockStock)
  {
    this.mockStock = mockStock;
  }

  @Override public void create(Stock stock)
  {

  }

  @Override public void update(Stock stock)
  {

  }

  @Override public void delete(String symbol)
  {

  }

  @Override public Stock getBySymbol(String symbol)
  {
    if (mockStock != null && mockStock.getSymbol().equals(symbol))
      return mockStock;

    return null;
  }

  @Override public List<Stock> getAll()
  {
    return List.of();
  }
}
