package Shared.Configuration;

import java.math.BigDecimal;

public class AppConfig
{
  private static AppConfig appConfig;

  private final BigDecimal startingBalance;
  private final BigDecimal transactionFee;
  private final int updateFrequencyInMs;
  private final BigDecimal stockResetValue;

  private AppConfig(BigDecimal startingBalance, BigDecimal transactionFee,
      int updateFrequencyInMs, BigDecimal stockResetValue)
  {
    this.startingBalance = startingBalance;
    this.transactionFee = transactionFee;
    this.updateFrequencyInMs = updateFrequencyInMs;
    this.stockResetValue = stockResetValue;
  }

  public AppConfig getInstance(BigDecimal startingBalance, BigDecimal transactionFee, int updateFrequencyInMs, BigDecimal stockResetValue)
  {
    if (appConfig == null)
    {
      appConfig = new AppConfig(startingBalance, transactionFee, updateFrequencyInMs, stockResetValue);
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
