package presentation.core;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewManager
{
    private static BorderPane mainLayout;
    private static ControllerFactory controllerFactory;
    private static final String FXML_DIR = "/fxml/";

    public static void init(Stage primaryStage, String initialView,
            ControllerFactory factory) throws IOException
    {
        controllerFactory = factory;

        URL resource = ViewManager.class.getResource(FXML_DIR + initialView + ".fxml");
        if (resource == null)
            throw new IOException("FXML resource not found: " + FXML_DIR + initialView + ".fxml");

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(controllerFactory);

        BorderPane root = loader.load();
        mainLayout = root;

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stock Trading App");
        primaryStage.show();

        var css = ViewManager.class.getResource("/css/fx.css");
        if (css != null)
        {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    public static void showView(String viewName)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            mainLayout.setCenter(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error loading view '" + viewName + "': " + e.getMessage()).show();
        }
    }

    public static void showView(String viewName, String argument)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            AcceptsStringArgument controller = (AcceptsStringArgument) loader.getController();
            controller.setArgument(argument);
            mainLayout.setCenter(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error loading view '" + viewName + "': " + e.getMessage()).show();
        }
    }

    public static void openWindow(String viewName, String title)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error opening window '" + viewName + "': " + e.getMessage()).show();
        }
    }

    public static void openWindow(String viewName, String title, String argument)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            AcceptsStringArgument controller = (AcceptsStringArgument) loader.getController();
            controller.setArgument(argument);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error opening window '" + viewName + "': " + e.getMessage()).show();
        }
    }

    public static ControllerFactory getControllerFactory()
    {
        return controllerFactory;
    }

    public static void openModalWindow(String viewName, String title)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (mainLayout.getScene() != null && mainLayout.getScene().getWindow() != null)
                stage.initOwner(mainLayout.getScene().getWindow());
            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error opening window '" + viewName + "': " + e.getMessage()).show();
        }
    }

    public static void openModalWindow(String viewName, String title, String argument)
    {
        try
        {
            URL resource = ViewManager.class.getResource(FXML_DIR + viewName + ".fxml");
            if (resource == null)
                throw new IOException("FXML resource not found: " + FXML_DIR + viewName + ".fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();
            AcceptsStringArgument controller = (AcceptsStringArgument) loader.getController();
            controller.setArgument(argument);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            if (mainLayout.getScene() != null && mainLayout.getScene().getWindow() != null)
                stage.initOwner(mainLayout.getScene().getWindow());
            stage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new Alert(AlertType.ERROR, "Error opening window '" + viewName + "': " + e.getMessage()).show();
        }
    }
}
