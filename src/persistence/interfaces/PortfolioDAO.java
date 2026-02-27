package persistence.interfaces;

import domain.Portfolio;

import java.util.List;
import java.util.UUID;

public interface PortfolioDAO
{
  void create(Portfolio portfolio);
  void update(Portfolio portfolio);
  void delete(UUID id);
  Portfolio getById(UUID id);
  List<Portfolio> getAll();
}
