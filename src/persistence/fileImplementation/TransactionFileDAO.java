package persistence.fileImplementation;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;
import persistence.interfaces.UnitOfWork;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionFileDAO implements TransactionDAO
{
  private final UnitOfWork uow;

  public TransactionFileDAO(UnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void create(Transaction transaction)
  {
    uow.getTransactions().add(transaction);
  }

  @Override public void update(Transaction transaction)
  {
    List<Transaction> allTransactions = uow.getTransactions();

    for (int i = 0; i < allTransactions.size(); i++)
    {
      if (allTransactions.get(i).getId().equals(transaction.getId()))
      {
        allTransactions.set(i, transaction);
        break;
      }
    }
  }

  @Override public void delete(UUID id)
  {
    uow.getTransactions().removeIf(t -> t.getId().equals(id));
  }

  @Override public Transaction getById(UUID id)
  {
    return uow.getTransactions().stream().filter(t ->t.getId().equals(id)).findFirst().orElse(null);
  }

  @Override public List<Transaction> getAll()
  {
    //Return copy
    return new ArrayList<>(uow.getTransactions());
  }
}
