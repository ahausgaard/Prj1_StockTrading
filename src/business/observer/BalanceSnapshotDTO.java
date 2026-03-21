package business.observer;

import domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record BalanceSnapshotDTO(
    Instant timestamp,
    BigDecimal balanceAfter,
    TransactionType transactionType,
    String stockSymbol,
    BigDecimal changeAmount)
{
}

