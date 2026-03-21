package business.observer;

import domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionDTO(
    UUID id,
    TransactionType type,
    String stockSymbol,
    double quantity,
    BigDecimal pricePerShare,
    BigDecimal totalAmount,
    BigDecimal fee,
    Instant timestamp)
{
}

