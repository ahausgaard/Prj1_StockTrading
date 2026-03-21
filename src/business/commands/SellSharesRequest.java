package business.commands;

import java.util.UUID;

public record SellSharesRequest(
    UUID portfolioId,
    String stockSymbol,
    double quantity)
{
}

