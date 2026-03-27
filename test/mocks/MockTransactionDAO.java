package mocks;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockTransactionDAO implements TransactionDAO
{
  private final List<Transaction> store = new ArrayList<>();

  @Override public void create(Transaction transaction)
  {
    store.add(transaction);
  }

  @Override public void update(Transaction transaction)
  {
    for (int i = 0; i < store.size(); i++)
    {
      if (store.get(i).getId().equals(transaction.getId()))
      {
        store.set(i, transaction);
        return;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return store.removeIf(t -> t.getId().equals(id));
  }

  @Override public Transaction getById(UUID id)
  {
    return store.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override public List<Transaction> getAll()
  {
    return List.copyOf(store);
  }

  @Override public List<Transaction> getByPortfolioId(UUID portfolioId)
  {
    return store.stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .toList();
  }
}
