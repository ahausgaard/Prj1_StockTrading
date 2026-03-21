package persistence.interfaces;

import domain.OwnedStock;

import java.util.List;
import java.util.UUID;

public interface OwnedStockDAO
{
  void create(OwnedStock ownedStock);
  void update(OwnedStock ownedStock);
  boolean delete(String symbol);
  OwnedStock getBySymbol(String symbol);
  List<OwnedStock> getAll();
  List<OwnedStock> getByPortfolioId(UUID portfolioId);
}
