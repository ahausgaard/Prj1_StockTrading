import javafx.application.Application;
import javafx.stage.Stage;
import presentation.core.ApplicationContext;
import presentation.core.ViewManager;

public class main extends Application
{

  @Override public void start(Stage primaryStage) throws Exception
  {
    ApplicationContext context = new ApplicationContext();
    ViewManager.init(primaryStage, "MainMenu", context.getControllerFactory());
  }

}
