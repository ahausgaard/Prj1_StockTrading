package persistence.fileImplementation;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionFileDAO implements TransactionDAO
{
  private final FileUnitOfWork uow;

  public TransactionFileDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Transaction transaction)
  {
    uow.getTransactions().add(transaction);
  }

  @Override public void update(Transaction transaction)
  {
    List<Transaction> all = uow.getTransactions();
    for (int i = 0; i < all.size(); i++)
    {
      if (all.get(i).getId().equals(transaction.getId()))
      {
        all.set(i, transaction);
        return;
      }
    }
  }

  @Override public boolean delete(UUID id)
  {
    return uow.getTransactions().removeIf(t -> t.getId().equals(id));
  }

  @Override public Transaction getById(UUID id)
  {
    return uow.getTransactions().stream()
        .filter(t -> t.getId().equals(id))
        .findFirst().orElse(null);
  }

  @Override public List<Transaction> getAll()
  {
    return new ArrayList<>(uow.getTransactions());
  }

  @Override public List<Transaction> getByPortfolioId(UUID portfolioId)
  {
    return uow.getTransactions().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId))
        .toList();
  }
}
