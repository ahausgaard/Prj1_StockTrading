package business.requests;

import java.util.UUID;

public record BuyStockRequest(UUID portfolioId, String stockSymbol, double quantity)
{
}
