package persistence.fileImplementation;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OwnedStockFileDAO implements OwnedStockDAO
{
  private final FileUnitOfWork uow;

  public OwnedStockFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(OwnedStock ownedStock)
  {
    uow.getOwnedStocks().add(ownedStock);
  }

  @Override public void update(OwnedStock ownedStock)
  {
    List<OwnedStock> all = uow.getOwnedStocks();
    for (int i = 0; i < all.size(); i++)
    {
      if (all.get(i).getStockSymbol().equals(ownedStock.getStockSymbol()))
      {
        all.set(i, ownedStock);
        return;
      }
    }
  }

  @Override public boolean delete(String symbol)
  {
    return uow.getOwnedStocks().removeIf(os -> os.getStockSymbol().equals(symbol));
  }

  @Override public OwnedStock getBySymbol(String symbol)
  {
    return uow.getOwnedStocks().stream()
        .filter(os -> os.getStockSymbol().equals(symbol))
        .findFirst().orElse(null);
  }

  @Override public List<OwnedStock> getAll()
  {
    return new ArrayList<>(uow.getOwnedStocks());
  }
}
