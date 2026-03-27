package mocks;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MockOwnedStockDAO implements OwnedStockDAO
{
  private final Map<String, OwnedStock> ownedStocks = new HashMap<>();

  @Override public void create(OwnedStock ownedStock)
  {
    ownedStocks.put(ownedStock.getStockSymbol(), ownedStock);
  }

  @Override public void update(OwnedStock ownedStock)
  {
    ownedStocks.put(ownedStock.getStockSymbol(), ownedStock);
  }

  @Override public boolean delete(String symbol)
  {
    return ownedStocks.remove(symbol) != null;
  }

  @Override public OwnedStock getBySymbol(String symbol)
  {
    return ownedStocks.get(symbol);
  }

  @Override public List<OwnedStock> getAll()
  {
    return new ArrayList<>(ownedStocks.values());
  }

  @Override public List<OwnedStock> getByPortfolioId(UUID portfolioId)
  {
    return ownedStocks.values().stream()
      .filter(os -> os.getPortfolioId().equals(portfolioId))
      .collect(Collectors.toList());
  }
}
