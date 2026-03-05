package domain;

import java.util.UUID;

public class OwnedStock
{
  private final UUID id;
  private final UUID portfolioId;
  private final String stockSymbol;
  private double numberOfShares;

  public OwnedStock(UUID id, UUID portfolioId, String stockSymbol, double numberOfShares)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  // Factory method for new OwnedStock (generates new UUID)
  public static OwnedStock createNew(UUID portfolioId, String stockSymbol, double numberOfShares) {
    return new OwnedStock(UUID.randomUUID(), portfolioId, stockSymbol, numberOfShares);
  }

  // Factory method for recreating from storage (uses existing UUID)
  public static OwnedStock recreateFromStorage(UUID id, UUID portfolioId, String stockSymbol, double numberOfShares) {
    return new OwnedStock(id, portfolioId, stockSymbol, numberOfShares);
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

  public double getNumberOfShares()
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
