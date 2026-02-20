package Persistence.FileImplementation;

import Persistence.Interfaces.UnitOfWork;

public class FileUnitOfWork implements UnitOfWork
{
  private String directoryPath;

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
  }
}
