package unit.mocks;

import domain.Portfolio;
import persistence.interfaces.PortfolioDAO;

import java.util.List;
import java.util.UUID;

public class MockPortfolioDAO implements PortfolioDAO
{
  private Portfolio mockPortfolio;

  public Portfolio getMockPortfolio()
  {
    return mockPortfolio;
  }

  public void setMockPortfolio(Portfolio mockPortfolio)
  {
    this.mockPortfolio = mockPortfolio;
  }

  @Override public void create(Portfolio portfolio)
  {
    this.mockPortfolio = portfolio;
  }

  @Override public void update(Portfolio portfolio)
  {

  }

  @Override public boolean delete(UUID id)
  {
    if (mockPortfolio != null && mockPortfolio.getId().equals(id))
    {
      mockPortfolio = null;
      return true;
    }
    return false;
  }

  @Override public Portfolio getById(UUID id)
  {
    return mockPortfolio != null && mockPortfolio.getId().equals(id) ? mockPortfolio : null;
  }

  @Override public List<Portfolio> getAll()
  {
    return mockPortfolio != null ? List.of(mockPortfolio) : List.of();
  }
}
