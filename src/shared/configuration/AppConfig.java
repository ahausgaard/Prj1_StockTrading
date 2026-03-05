package shared.configuration;

public class AppConfig
{
  private static AppConfig appConfig;

  private final int startingBalance;
  private final double transactionFee;
  private final int updateFrequencyInMs;
  private final double stockResetValue;

  private AppConfig()
  {
    this.startingBalance = 10000;
    this.transactionFee = 0.01;
    this.updateFrequencyInMs = 1000;
    this.stockResetValue = 500.0;
  }

  //Bill Pugh Pattern
  private static class ConfigHolder {
    private static final AppConfig INSTANCE = new AppConfig();
  }

  public static AppConfig getInstance()
  {
    return ConfigHolder.INSTANCE;
  }

  public int getStartingBalance()
  {
    return startingBalance;
  }

  public double getTransactionFee()
  {
    return transactionFee;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }

  public double getStockResetValue()
  {
    return stockResetValue;
  }
}
