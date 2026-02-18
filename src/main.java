import Shared.Logging.Logger;
import Shared.Logging.LoggerLevel;

public class main
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    logger.log(LoggerLevel.INFO, "Application started");
    logger.log(LoggerLevel.WARNING, "Stock not found in database");
    logger.log(LoggerLevel.ERROR, "Failed to save data: ", new Error("LOLSKY") );
  }
}
