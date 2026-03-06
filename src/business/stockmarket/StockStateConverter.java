package business.stockmarket;

import business.stockmarket.simulation.*;
import domain.StockState;

public class StockStateConverter
{
  public static LiveStockState toLiveState(StockState state)
  {
    return switch (state)
    {
      case STEADY    -> new SteadyState();
      case GROWING   -> new GrowingState();
      case DECLINING -> new DecliningState();
      case BANKRUPT  -> new BankruptState();
      case RESET     -> new ResetState();
    };
  }

  public static StockState toDomainState(LiveStockState state)
  {
    return switch (state)
    {
      case SteadyState    s -> StockState.STEADY;
      case GrowingState   s -> StockState.GROWING;
      case DecliningState s -> StockState.DECLINING;
      case BankruptState  s -> StockState.BANKRUPT;
      case ResetState     s -> StockState.RESET;
      default -> throw new IllegalArgumentException("Unknown state: " + state.getClass().getSimpleName());
    };
  }
}
