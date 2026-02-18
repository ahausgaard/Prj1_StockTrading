package Shared.Configuration;

public class AppConfig
{
  private static AppConfig appConfig;

  private final int startingBalance;
  private final double transactionFee;
  private final int updateFrequencyInMs;
  private final double stockResetValue;

  private AppConfig(int startingBalance, double transactionFee,
      int updateFrequencyInMs, double stockResetValue)
  {
    this.startingBalance = startingBalance;
    this.transactionFee = transactionFee;
    this.updateFrequencyInMs = updateFrequencyInMs;
    this.stockResetValue = stockResetValue;
  }

  public AppConfig getInstance(int startingBalance, double transactionFee, int updateFrequencyInMs, double stockResetValue)
  {
    if (appConfig == null)
    {
      appConfig = new AppConfig(startingBalance, transactionFee, updateFrequencyInMs, stockResetValue);
    }
    return appConfig;
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
