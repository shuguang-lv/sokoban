package Debug;

import Engine.GameEngine;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * singleton class
 */
public final class GameLogger extends Logger {

    private static final Logger logger = Logger.getLogger("GameLogger");
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final Calendar calendar = Calendar.getInstance();

    private GameLogger() throws IOException {
        super("Sokoban", null);

        File directory = new File(System.getProperty("user.dir") + "/" + "logs");
        directory.mkdirs();

        FileHandler fileHandler = new FileHandler(directory + "/" + GameEngine.getGameName() + ".log"); // variable name changed
        logger.addHandler(fileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
    }

    private static String createFormattedMessage(String message) {
        return dateFormat.format(calendar.getTime()) + " -- " + message;
    }

    public static void showInfo(String message) {
        logger.info(createFormattedMessage(message));
    }

    public static void showWarning(String message) {
        logger.warning(createFormattedMessage(message));
    }

    public static void showSevere(String message) {
        logger.severe(createFormattedMessage(message));
    }
}