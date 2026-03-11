package persistence.fileImplementation;

import domain.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StockPriceHistoryFileDAO implements StockPriceHistoryDAO
{
  private final FileUnitOfWork uow;

  public StockPriceHistoryFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(StockPriceHistory stockPriceHistory)
  {
    uow.getStockPriceHistories().add(stockPriceHistory);
  }

  @Override public void update(StockPriceHistory stockPriceHistory)
  {
    List<StockPriceHistory> all = uow.getStockPriceHistories();
    for (int i = 0; i < all.size(); i++)
    {
      if (all.get(i).getId().equals(stockPriceHistory.getId()))
      {
        all.set(i, stockPriceHistory);
        return;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return uow.getStockPriceHistories().removeIf(sph -> sph.getId().equals(id));
  }

  @Override public StockPriceHistory getById(UUID id)
  {
    return uow.getStockPriceHistories().stream()
        .filter(sph -> sph.getId().equals(id))
        .findFirst().orElse(null);
  }

  @Override public List<StockPriceHistory> getAll()
  {
    return new ArrayList<>(uow.getStockPriceHistories());
  }
}
