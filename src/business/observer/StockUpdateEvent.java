package business.observer;

import java.util.List;

public record StockUpdateEvent(List<StockDTO> stocks)
{
}

