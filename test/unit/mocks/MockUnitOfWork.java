package unit.mocks;

import persistence.interfaces.UnitOfWork;

public class MockUnitOfWork implements UnitOfWork
{
  private int beginCount = 0;
  private int commitCount = 0;
  private int rollbackCount = 0;

  @Override public void begin()
  {
    beginCount ++;
  }

  @Override public void commit()
  {
    commitCount ++;
  }

  @Override public void rollback()
  {
    rollbackCount ++;
  }

  public int getBeginCount()
  {
    return beginCount;
  }

  public int getCommitCount()
  {
    return commitCount;
  }

  public int getRollbackCount()
  {
    return rollbackCount;
  }
}
