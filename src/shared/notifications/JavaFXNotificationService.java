package shared.notifications;

import javafx.scene.control.Alert;

public class JavaFXNotificationService implements NotificationService {

    @Override
    public void showInfo(String title, String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    @Override
    public void showWarning(String title, String message) {
        new Alert(Alert.AlertType.WARNING, message).show();
    }

    @Override
    public void showError(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}
