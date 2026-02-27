package persistence.fileImplementation;

import domain.StockPriceHistory;
import persistence.interfaces.StockPurchaseDAO;
import persistence.interfaces.UnitOfWork;

import java.util.List;
import java.util.UUID;

public class StockPurchaseFileDAO implements StockPurchaseDAO
{
  private final UnitOfWork uow;

  public StockPurchaseFileDAO(UnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(StockPriceHistory stockPriceHistory)
  {

  }

  @Override public void delete(UUID id)
  {

  }

  @Override public StockPriceHistory getById(UUID id)
  {
    return null;
  }

  @Override public List<StockPriceHistory> getAll()
  {
    return List.of();
  }
}
