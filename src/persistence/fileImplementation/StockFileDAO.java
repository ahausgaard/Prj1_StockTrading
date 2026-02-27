package persistence.fileImplementation;

import domain.Stock;
import persistence.interfaces.StockDAO;
import persistence.interfaces.UnitOfWork;

import java.util.ArrayList;
import java.util.List;

public class StockFileDAO implements StockDAO
{
  private final UnitOfWork uow;

  public StockFileDAO(UnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Stock stock)
  {
    uow.getStocks().add(stock);
  }

  @Override public void update(Stock stock)
  {
    List<Stock> allStocks = uow.getStocks();

    for (int i = 0; i < allStocks.size(); i++)
    {
      if (allStocks.get(i).getSymbol().equals(stock.getSymbol()))
      {
        allStocks.set(i, stock);
        break;
      }
    }
  }

  @Override public void delete(String symbol)
  {
    uow.getStocks().removeIf(s -> s.getSymbol().equals(symbol));
  }

  @Override public Stock getBySymbol(String symbol)
  {
    return uow.getStocks().stream().filter(s -> s.getSymbol().equals(symbol)).findFirst().orElse(null);
  }

  @Override public List<Stock> getAll()
  {
    //Return copy
    return new ArrayList<>(uow.getStocks());
  }
}
