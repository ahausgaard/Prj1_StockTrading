package persistence.interfaces;

import domain.Stock;

import java.util.List;

public interface UnitOfWork
{
  void begin();
  void commit();
  void rollback();
}
