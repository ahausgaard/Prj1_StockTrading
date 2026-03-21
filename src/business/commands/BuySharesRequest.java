package business.commands;

import java.util.UUID;

public record BuySharesRequest(UUID portfolioId, String stockSymbol, double quantity)
{
}

