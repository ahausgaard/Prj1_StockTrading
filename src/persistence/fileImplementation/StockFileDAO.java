package persistence.fileImplementation;

import domain.Stock;
import persistence.interfaces.StockDAO;

import java.util.ArrayList;
import java.util.List;

public class StockFileDAO implements StockDAO
{
  private final FileUnitOfWork uow;

  public StockFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Stock stock)
  {
    uow.getStocks().add(stock);
  }

  @Override public void update(Stock stock)
  {
    List<Stock> all = uow.getStocks();
    for (int i = 0; i < all.size(); i++)
    {
      if (all.get(i).getSymbol().equals(stock.getSymbol()))
      {
        all.set(i, stock);
        return;
      }
    }
  }

  @Override public void delete(String symbol)
  {
    uow.getStocks().removeIf(s -> s.getSymbol().equals(symbol));
  }

  @Override public Stock getBySymbol(String symbol)
  {
    return uow.getStocks().stream()
        .filter(s -> s.getSymbol().equals(symbol))
        .findFirst().orElse(null);
  }

  @Override public List<Stock> getAll()
  {
    return new ArrayList<>(uow.getStocks());
  }
}
