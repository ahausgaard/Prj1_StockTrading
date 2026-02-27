package persistence.interfaces;

import domain.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionDAO
{
  void create(Transaction transaction);
  void update(Transaction transaction);
  void delete(UUID id);
  Transaction getById(UUID id);
  List<Transaction> getAll();
}
