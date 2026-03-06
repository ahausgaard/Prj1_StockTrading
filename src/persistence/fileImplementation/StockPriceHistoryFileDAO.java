package persistence.fileImplementation;

import domain.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;
import persistence.interfaces.UnitOfWork;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StockPriceHistoryFileDAO implements StockPriceHistoryDAO
{
  private final UnitOfWork uow;

  public StockPriceHistoryFileDAO(UnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(StockPriceHistory stockPriceHistory)
  {
    uow.getStockPriceHistories().add(stockPriceHistory);
  }

  @Override public void update(StockPriceHistory stockPriceHistory)
  {
    List<StockPriceHistory> allStockPriceHistories = uow.getStockPriceHistories();

    for (int i = 0; i < allStockPriceHistories.size(); i++)
    {
      if (allStockPriceHistories.get(i).getId().equals(stockPriceHistory.getId()))
      {
        allStockPriceHistories.set(i, stockPriceHistory);
        break;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return uow.getStockPriceHistories().removeIf(sph -> sph.getId().equals(id));
  }

  @Override public StockPriceHistory getById(UUID id)
  {
    return uow.getStockPriceHistories().stream().filter(sph -> sph.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<StockPriceHistory> getAll()
  {
    //return copy
    return new ArrayList<>(uow.getStockPriceHistories());
  }
}
