package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction
{
  private final UUID id;
  private final TransactionType type;
  private final UUID portfolioId;
  private final String stockSymbol;
  private final double quantity;
  private final BigDecimal pricePerShare;
  private final BigDecimal totalAmount;
  private final BigDecimal fee;
  private final Instant timestamp;

  public Transaction(UUID id, UUID portfolioId, TransactionType type, String stockSymbol, double quantity, BigDecimal pricePerShare, BigDecimal totalAmount, BigDecimal fee, Instant timestamp )
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.type = type;
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
    this.pricePerShare = pricePerShare;
    this.totalAmount = totalAmount;
    this.fee = fee;
    this.timestamp = timestamp;
  }

  public static Transaction createNew(UUID portfolioId, TransactionType type, String stockSymbol, double quantity, BigDecimal pricePerShare, BigDecimal totalAmount, BigDecimal fee, Instant timestamp) {
    return new Transaction(UUID.randomUUID(), portfolioId, type, stockSymbol, quantity, pricePerShare, totalAmount, fee, timestamp);
  }

  public UUID getId()
  {
    return id;
  }

  public TransactionType getType()
  {
    return type;
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

  public BigDecimal getPricePerShare()
  {
    return pricePerShare;
  }

  public BigDecimal getTotalAmount()
  {
    return totalAmount;
  }

  public BigDecimal getFee()
  {
    return fee;
  }

  public Instant getTimestamp()
  {
    return timestamp;
  }
}
