package Shared.Configuration;

import java.math.BigDecimal;

public class AppConfig
{
  private static AppConfig appConfig;

  private final BigDecimal startingBalance;
  private final BigDecimal transactionFee;
  private final int updateFrequencyInMs;
  private final BigDecimal stockResetValue;

  private AppConfig()
  {
    this.startingBalance = BigDecimal.valueOf(15000);
    this.transactionFee = BigDecimal.valueOf(0.15);
    this.updateFrequencyInMs = 1000;
    this.stockResetValue = BigDecimal.valueOf(150);
  }

  public AppConfig getInstance(BigDecimal startingBalance, BigDecimal transactionFee, int updateFrequencyInMs, BigDecimal stockResetValue)
  {
    if (appConfig == null)
    {
      appConfig = new AppConfig();
    }
    return appConfig;
  }

  public BigDecimal getStartingBalance()
  {
    return startingBalance;
  }

  public BigDecimal getTransactionFee()
  {
    return transactionFee;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }

  public BigDecimal getStockResetValue()
  {
    return stockResetValue;
  }
}

