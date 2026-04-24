package unit.mocks;

import domain.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockStockPriceHistoryDAO implements StockPriceHistoryDAO
{
  private final List<StockPriceHistory> store = new ArrayList<>();

  @Override public void create(StockPriceHistory stockPriceHistory)
  {
    store.add(stockPriceHistory);
  }

  @Override public void update(StockPriceHistory stockPriceHistory)
  {
    for (int i = 0; i < store.size(); i++)
    {
      if (store.get(i).getId().equals(stockPriceHistory.getId()))
      {
        store.set(i, stockPriceHistory);
        return;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return store.removeIf(h -> h.getId().equals(id));
  }

  @Override public StockPriceHistory getById(UUID id)
  {
    return store.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override public List<StockPriceHistory> getAll()
  {
    return List.copyOf(store);
  }
}

