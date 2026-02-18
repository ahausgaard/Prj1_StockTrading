package Shared.Logging;

public interface LogOutput
{
  void log(LoggerLevel level, String message);
  void log(LoggerLevel level, String message, Error exception);
}
