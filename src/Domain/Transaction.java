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
  private BigDecimal quantity;
  private BigDecimal pricePerShare;
  private BigDecimal totalAmount;
  private BigDecimal fee;
  private Instant timestamp;

  public Transaction(UUID id, UUID portfolioId, TransactionType type, String stockSymbol)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.type = type;
    this.stockSymbol = stockSymbol;
  }
}
