package domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private BigDecimal currentBalance = BigDecimal.ZERO;

  public Portfolio(UUID id, BigDecimal currentBalance)
  {
    this.id = id;
    this.currentBalance = currentBalance;
  }

  // Factory method for new Portfolio (generates new UUID)
  public static Portfolio createNew(BigDecimal initialBalance) {
    return new Portfolio(UUID.randomUUID(), initialBalance);
  }

  // Factory method for recreating from storage (uses existing UUID)
  public static Portfolio recreateFromStorage(UUID id, BigDecimal currentBalance) {
    return new Portfolio(id, currentBalance);
  }

  public void setCurrentBalance(BigDecimal currentBalance)
  {
    this.currentBalance = currentBalance;
  }

  public BigDecimal getCurrentBalance()
  {
    return currentBalance;
  }

  public UUID getId()
  {
    return id;
  }

  @Override public String toString()
  {
    return "Portfolio{" + "id=" + id + ", currentBalance=" + currentBalance
        + '}';
  }
}
