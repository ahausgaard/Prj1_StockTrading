package persistence.interfaces;

import domain.OwnedStock;

import java.util.List;
import java.util.UUID;

public interface OwnedStockDAO
{
  void create(OwnedStock ownedStock);
  void delete(UUID id);
  OwnedStock getById(UUID id);
  List<OwnedStock> getAll();
}
