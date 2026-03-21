package business.observer;

import business.dto.StockDTO;
import java.util.List;

public record StockUpdateEvent(List<StockDTO> stocks)
{
}

