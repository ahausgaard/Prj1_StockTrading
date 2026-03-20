package business.requests;

public record SellSharesRequest(
    String portfolioId,
    String stockSymbol,
    double quantity)
{
}
