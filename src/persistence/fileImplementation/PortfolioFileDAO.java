package persistence.fileImplementation;

import domain.Portfolio;
import persistence.interfaces.PortfolioDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortfolioFileDAO implements PortfolioDAO
{
  private final FileUnitOfWork uow;

  public PortfolioFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Portfolio portfolio)
  {
    uow.getPortfolios().add(portfolio);
  }

  @Override public void update(Portfolio portfolio)
  {
    List<Portfolio> all = uow.getPortfolios();
    for (int i = 0; i < all.size(); i++)
    {
      if (all.get(i).getId().equals(portfolio.getId()))
      {
        all.set(i, portfolio);
        return;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return uow.getPortfolios().removeIf(p -> p.getId().equals(id));
  }

  @Override public Portfolio getById(UUID id)
  {
    return uow.getPortfolios().stream()
        .filter(p -> p.getId().equals(id))
        .findFirst().orElse(null);
  }

  @Override public List<Portfolio> getAll()
  {
    return new ArrayList<>(uow.getPortfolios());
  }
}
