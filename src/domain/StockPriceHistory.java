package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class StockPriceHistory
{
  private final UUID id;
  private final String stockSymbol;
  private BigDecimal price;
  private Instant timestamp;

  public StockPriceHistory(UUID id, String stockSymbol, BigDecimal price, Instant timestamp)
  {
    this.id = id;
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timestamp = timestamp;
  }

  public UUID getId()
  {
    return id;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public BigDecimal getPrice()
  {
    return price;
  }

  public Instant getTimestamp()
  {
    return timestamp;
  }
}
