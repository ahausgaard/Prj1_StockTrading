package Shared.Logging;

public class ConsoleLogOutput implements LogOutput
{
  @Override public void log(LoggerLevel level, String message)
  {
    System.out.println("[" + level + "] " + message);
  }
}
