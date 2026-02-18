package Shared.Logging;

public class ConsoleLogOutput implements LogOutput
{
  @Override public void log(LoggerLevel level, String message)
  {
    System.out.println("[" + level + "] " + message);
  }

  @Override public void log(LoggerLevel level, String message, Error exception)
  {
    System.out.println("[" + level + "] " + message + " " + exception);
  }
}
