import business.dto.StockDTO;
import business.services.market.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import domain.Stock;
import javafx.application.Application;
import javafx.stage.Stage;
import persistence.fileImplementation.FileUnitOfWork;
import persistence.fileImplementation.StockFileDAO;
import persistence.fileImplementation.StockPriceHistoryFileDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.StockPriceHistoryDAO;
import shared.configuration.AppConfig;
import java.math.BigDecimal;


public class main extends Application
{

  @Override public void start(Stage primaryStage) throws Exception
  {

  }

}


