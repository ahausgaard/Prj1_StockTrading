package persistence.fileImplementation;

import persistence.interfaces.UnitOfWork;

public class FileUnitOfWork implements UnitOfWork
{
  private String directoryPath;

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
  }
}
