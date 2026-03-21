package business.stockmarket;

import business.dto.StockDTO;
import business.observer.StockMarketSubject;
import business.observer.StockUpdateEvent;
import business.observer.StockMarketObserver;
import business.stockmarket.simulation.LiveStock;
import domain.Stock;
import shared.logging.Logger;
import shared.logging.LoggerLevel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static business.stockmarket.simulation.LiveStock.fromExisting;

public class StockMarket implements StockMarketSubject
{
  private final Logger logger = Logger.getInstance();
  private final List<LiveStock> liveStocks = new CopyOnWriteArrayList<>(); //For thread safety
  private final List<StockMarketObserver> observers = new CopyOnWriteArrayList<>();

  public void addNewStock(String stockSymbol)
  {
    liveStocks.add(LiveStock.createNew(stockSymbol.toUpperCase()));
    logger.log(LoggerLevel.INFO, "Added new stock: " + stockSymbol.toUpperCase());
  }

  public void addExistingStock(Stock stock)
  {
    liveStocks.add(fromExisting(stock));
    logger.log(LoggerLevel.INFO, "Added existing stock: " + stock.getSymbol());
  }

  public void updateAllLiveStocks()
  {
    for (LiveStock liveStock : liveStocks)
    {
      liveStock.updatePrice();
    }
    notifyObservers();
  }

  private void notifyObservers()
  {
    List<StockDTO> stockDTOs = liveStocks.stream()
        .map(ls -> new StockDTO(
            ls.getSymbol(),
            ls.getCurrentPrice(),
            StockStateConverter.toDomainState(ls.getCurrentState())))
        .toList();

    StockUpdateEvent event = new StockUpdateEvent(stockDTOs);

    for (StockMarketObserver observer : observers)
    {
      observer.update(event);
    }
  }

  @Override
  public void addObserver(StockMarketObserver observer)
  {
    observers.add(observer);
  }

  @Override
  public void removeObserver(StockMarketObserver observer)
  {
    observers.remove(observer);
  }

  public List<Stock> getAllStocks()
  {
    return liveStocks.stream().map(LiveStock::toStock).toList();
  }

  // Bill Pugh Singleton implementation
  private StockMarket() {}

  private static class StockMarketHolder {
    private static final StockMarket INSTANCE = new StockMarket();
  }

  public static StockMarket getInstance() {
    return StockMarketHolder.INSTANCE;
  }
}
