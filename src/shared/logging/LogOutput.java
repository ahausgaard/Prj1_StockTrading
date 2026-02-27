package shared.logging;

public interface LogOutput
{
  void log(LoggerLevel level, String message);
  void log(LoggerLevel level, String message, Throwable exception);
}
