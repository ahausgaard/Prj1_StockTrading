package business.dto;

import java.util.List;

public record StockUpdateEvent(List<StockDTO> stocks)
{
}

