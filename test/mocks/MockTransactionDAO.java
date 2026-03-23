package mocks;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.List;
import java.util.UUID;

public class MockTransactionDAO implements TransactionDAO
{
  @Override public void create(Transaction transaction)
  {

  }

  @Override public void update(Transaction transaction)
  {

  }

  @Override public boolean delete(UUID id)
  {
    return false;
  }

  @Override public Transaction getById(UUID id)
  {
    return null;
  }

  @Override public List<Transaction> getAll()
  {
    return List.of();
  }

  @Override public List<Transaction> getByPortfolioId(UUID portfolioId)
  {
    return List.of();
  }
}
