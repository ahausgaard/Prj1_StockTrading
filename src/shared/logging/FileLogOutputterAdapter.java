package shared.logging;

import adapters.FileLogOutputter;

public class FileLogOutputterAdapter implements LogOutput {

    private final FileLogOutputter adaptee;

    public FileLogOutputterAdapter(FileLogOutputter adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void log(LoggerLevel level, String message) {
        switch (level) {
            case INFO    -> adaptee.logInfo(message);
            case WARNING -> adaptee.logWarning(message);
            case ERROR   -> adaptee.logError(message);
        }
    }

    @Override
    public void log(LoggerLevel level, String message, Throwable exception) {
        log(level, message + " | " + exception.getMessage());
    }
}
