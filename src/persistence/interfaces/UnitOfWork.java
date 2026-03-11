package persistence.interfaces;

public interface UnitOfWork
{
  void begin();
  void commit();
  void rollback();
}
