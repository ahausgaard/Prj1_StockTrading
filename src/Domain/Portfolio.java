package Domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Portfolio
{
  private final UUID id;
  private BigDecimal currentBalance = BigDecimal.ZERO;



  public Portfolio(UUID id, BigDecimal currentBalance)
  {
    if (id == null)
      this.id = UUID.randomUUID();
    else
      this.id = id;

    this.currentBalance = currentBalance;
  }

  public Portfolio(BigDecimal initialBalance) {
    this(UUID.randomUUID(), initialBalance);
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
