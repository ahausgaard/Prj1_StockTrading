package Domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction
{
  private final UUID id;
  private final TransactionType type;
  private final UUID portfolioId;
  private final String stockSymbol;
  private final BigDecimal quantity;
  private final BigDecimal pricePerShare;
  private final BigDecimal totalAmount;
  private final BigDecimal fee;
  private final Instant timestamp;

  public Transaction(UUID id, UUID portfolioId, TransactionType type, String stockSymbol, BigDecimal quantity, BigDecimal pricePerShare, BigDecimal totalAmount, BigDecimal fee, Instant timestamp )
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
}
