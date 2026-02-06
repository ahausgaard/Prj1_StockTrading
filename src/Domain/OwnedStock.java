package Domain;

import java.math.BigDecimal;
import java.util.UUID;

public class OwnedStock
{
  private final UUID id;
  private final UUID portfolioId;
  private String stockSymbol;
  private BigDecimal numberOfShares;

  public OwnedStock(UUID id, UUID portfolioId, String stockSymbol, BigDecimal numberOfShares)
  {
    if (id == null)
      this.id = UUID.randomUUID();
    else
      this.id = id;

    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  public OwnedStock(UUID portfolioId, String stockSymbol) {
    this(UUID.randomUUID(), portfolioId, stockSymbol, BigDecimal.ZERO);
  }

  public UUID getId()
  {
    return id;
  }

  public UUID getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public BigDecimal getNumberOfShares()
  {
    return numberOfShares;
  }

  @Override public String toString()
  {
    return "OwnedStock{" + "id=" + id + ", portfolioId=" + portfolioId
        + ", stockSymbol='" + stockSymbol + '\'' + ", numberOfShares="
        + numberOfShares + '}';
  }
}
