package shared.notifications;

public interface NotificationService {
    void showInfo(String title, String message);
    void showWarning(String title, String message);
    void showError(String title, String message);
}
