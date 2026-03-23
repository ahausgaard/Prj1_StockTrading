package mocks;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;

import java.util.List;
import java.util.UUID;

public class MockOwnedStockDAO implements OwnedStockDAO
{
  @Override public void create(OwnedStock ownedStock)
  {

  }

  @Override public void update(OwnedStock ownedStock)
  {

  }

  @Override public boolean delete(String symbol)
  {
    return false;
  }

  @Override public OwnedStock getBySymbol(String symbol)
  {
    return null;
  }

  @Override public List<OwnedStock> getAll()
  {
    return List.of();
  }

  @Override public List<OwnedStock> getByPortfolioId(UUID portfolioId)
  {
    return List.of();
  }
}
