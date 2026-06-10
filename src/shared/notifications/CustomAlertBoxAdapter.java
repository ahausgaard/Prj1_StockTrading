package shared.notifications;

import adapters.CustomAlertBox;

public class CustomAlertBoxAdapter implements NotificationService {

    private final CustomAlertBox adaptee;

    public CustomAlertBoxAdapter(CustomAlertBox adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void showInfo(String title, String message) {
        adaptee.showAlert(message, title, CustomAlertBox.AlertType.INFO);
    }

    @Override
    public void showWarning(String title, String message) {
        adaptee.showAlert(message, title, CustomAlertBox.AlertType.WARNING);
    }

    @Override
    public void showError(String title, String message) {
        adaptee.showAlert(message, title, CustomAlertBox.AlertType.ERROR);
    }
}
