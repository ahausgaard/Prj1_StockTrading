package persistence.fileImplementation;

import domain.Portfolio;
import domain.Transaction;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.UnitOfWork;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortfolioFileDAO implements PortfolioDAO
{
  private final UnitOfWork uow;

  public PortfolioFileDAO(UnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Portfolio portfolio)
  {
    uow.getPortfolios().add(portfolio);
  }

  @Override public void update(Portfolio portfolio)
  {
    List<Portfolio> allPortfolios = uow.getPortfolios();

    for (int i = 0; i < allPortfolios.size(); i++)
    {
      if (allPortfolios.get(i).getId().equals(portfolio.getId()))
      {
        allPortfolios.set(i, portfolio);
        break;
      }
    }
  }

  @Override public void delete(UUID id)
  {
    uow.getPortfolios().removeIf(p -> p.getId().equals(id));
  }

  @Override public Portfolio getById(UUID id)
  {
    return uow.getPortfolios().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<Portfolio> getAll()
  {
    //Return copy
    return new ArrayList<>(uow.getPortfolios());
  }
}
