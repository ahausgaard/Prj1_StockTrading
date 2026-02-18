package Shared.Logging;

public class Logger
{
  private LogOutput output;

  private Logger() {
    this.output = new ConsoleLogOutput();
  }

  private static class LoggerHolder{
    private static final Logger INSTANCE = new Logger(); //Bill Pugh Singleton
  }

  public static Logger getInstance()
  {
    return LoggerHolder.INSTANCE;
  }

  public void setOutput(LogOutput output)
  {
    this.output = output;
  }

  public void log(LoggerLevel level, String message)
  {
    if (output != null)
       output.log(level, message);
  }

  public void log(LoggerLevel level, String message, Error exception)
  {
    if (output != null)
      output.log(level, message, exception);
  }

}
