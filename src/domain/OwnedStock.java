package domain;

import java.util.UUID;

public class OwnedStock
{
  private final UUID id;
  private final UUID portfolioId;
  private final String stockSymbol;
  private double quantity;

  public OwnedStock(UUID id, UUID portfolioId, String stockSymbol, double quantity)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
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

  public double getQuantity()
  {
    return quantity;
  }

  public void addShares(double amount)
  {
    this.quantity += amount;
  }

  @Override public String toString()
  {
    return "OwnedStock{" + "id=" + id + ", portfolioId=" + portfolioId
        + ", stockSymbol='" + stockSymbol + '\'' + ", numberOfShares="
        + quantity + '}';
  }
}
