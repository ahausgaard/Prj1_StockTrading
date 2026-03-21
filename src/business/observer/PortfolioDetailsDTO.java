package business.observer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioDetailsDTO(UUID portfolioId, BigDecimal currentBalance, List<StockDTO> stocks)
{
}
