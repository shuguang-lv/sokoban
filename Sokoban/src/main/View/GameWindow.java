package View;

import Controller.GameEngine;
import Debug.GameLogger;
import Modal.*;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.effect.MotionBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * The type Game window.
 */
// GUI class extracted
public class GameWindow {
    /**
     * The Primary stage.
     */
    private static Stage primaryStage;
    /**
     * The Game grid.
     */
    private static GridPane gameGrid;
    /**
     * The Root.
     */
    private static GridPane root;
    /**
     * The Menu.
     */
    private static MenuBar menu;
    /**
     * The Mp.
     */
    private static MediaPlayer mediaPlayer = new MediaPlayer(new Media(GameFile.getFile("src/main/resources/music/bgm.wav").toURI().toString()));

    /**
     * Instantiates a new Game window.
     *
     * @param primaryStage the primary stage
     */
    public static void createGameWindow(Stage ps) {
        primaryStage = ps;
        createMenu();
        createPane();
        setEventFilter();
        reloadGrid();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /**
     * Create menu.
     */
    private static void createMenu() {
        menu = new MenuBar();
        Menu menuFile = createMenuFile();
        Menu menuLevel = createMenuLevel();
        Menu menuAbout = createMenuAbout();
        menu.getMenus().addAll(menuFile, menuLevel, menuAbout);
    }

    /**
     * Create menu about menu.
     *
     * @return the menu
     */
    private static Menu createMenuAbout() {
        MenuItem menuItemAbout = new MenuItem("About This Game");
        menuItemAbout.setOnAction(actionEvent -> showAbout());
        Menu menuAbout = new Menu("About");
        menuAbout.getItems().addAll(menuItemAbout);
        return menuAbout;
    }

    /**
     * Create menu level menu.
     *
     * @return the menu
     */
    private static Menu createMenuLevel() {
        MenuItem menuItemUndo = new MenuItem("Undo");
        menuItemUndo.setOnAction(actionEvent -> undo());
        RadioMenuItem radioMenuItemMusic = new RadioMenuItem("Toggle Music");
        radioMenuItemMusic.setOnAction(actionEvent -> toggleMusic());
        RadioMenuItem radioMenuItemDebug = new RadioMenuItem("Toggle Debug");
        radioMenuItemDebug.setOnAction(actionEvent -> toggleDebug());
        MenuItem menuItemResetLevel = new MenuItem("Reset Level");
        menuItemResetLevel.setOnAction(actionEvent -> resetLevel());
        Menu menuLevel = new Menu("Level");
        menuLevel.getItems().addAll(menuItemUndo, radioMenuItemMusic, radioMenuItemDebug,
                new SeparatorMenuItem(), menuItemResetLevel);
        return menuLevel;
    }

    /**
     * Create menu file menu.
     *
     * @return the menu
     */
    private static Menu createMenuFile() {
        MenuItem menuItemSaveGame = new MenuItem("Save Game");
        menuItemSaveGame.setOnAction(actionEvent -> saveGame());
        MenuItem menuItemLoadGame = new MenuItem("Load Game");
        menuItemLoadGame.setOnAction(actionEvent -> loadGame());
        MenuItem menuItemExit = new MenuItem("Exit");
        menuItemExit.setOnAction(actionEvent -> closeGame());
        Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(menuItemSaveGame, menuItemLoadGame, new SeparatorMenuItem(), menuItemExit);
        return menuFile;
    }

    /**
     * Create pane.
     */
    private static void createPane() {
        gameGrid = new GridPane();
        root = new GridPane();
        root.add(menu, 0, 0);
        root.add(gameGrid, 0, 1);
        primaryStage.setTitle(GameEngine.getGameEngine().getGameName());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Sets event filter.
     */
    public static void setEventFilter() {
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            GameEngine.getGameEngine().handleKey(event.getCode());
            reloadGrid();
        });
    }

    /**
     * Reload grid.
     */
    private static void reloadGrid() {
        if (GameEngine.getGameEngine().isGameComplete()) {
            HighScore.updateMap(0, GameEngine.getGameEngine().getMovesCount());
            showVictoryMessage();
            return;
        }

        Level currentLevel = GameEngine.getGameEngine().getCurrentLevel();
        Level.LevelIterator levelGridIterator = (Level.LevelIterator) currentLevel.iterator();
        gameGrid.getChildren().clear();

        while (levelGridIterator.hasNext()) {
            addObjectToGrid(levelGridIterator.next(), levelGridIterator.getCurrentPosition());
        }

        gameGrid.autosize();
        primaryStage.sizeToScene();
    }

    /**
     * Add object to grid.
     *
     * @param gameObject the game object
     * @param location   the location
     */
    private static void addObjectToGrid(GameObject gameObject, Point location) {
        GraphicObject graphicObject = new GraphicObject(gameObject);
        gameGrid.add(graphicObject, location.y, location.x);
    }

    /**
     * Close game.
     */
    private static void closeGame() {
        System.exit(0);
    }

    /**
     * Save game.
     */
    public static void saveGame() {
        GameFile.saveGameFile(primaryStage);
    }

    /**
     * Load game.
     */
    public static void loadGame() {
        try {
            Object fileInput;
            fileInput = GameFile.loadGameFile(primaryStage);

            if (fileInput != null) {
                if (fileInput instanceof GameEngine) {
                    GameEngine.createGameEngine((GameEngine) fileInput);
                } else {
                    GameEngine.createGameEngine((FileInputStream) fileInput);
                }
                reloadGrid();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Undo.
     */
    private static void undo() {
        History.traceHistory();
        reloadGrid();
    }

    /**
     * Reset level.
     */
    private static void resetLevel() {
        History.resetHistory();
        reloadGrid();
    }

    /**
     * Show victory message.
     */
    private static void showVictoryMessage() {
        String dialogTitle = "Game Over !";
        String dialogMessage = "You completed " + GameEngine.getGameEngine().getMapSetName() + " in " + GameEngine.getGameEngine().getMovesCount() + " moves!\n" +
                "High score in history: " + HighScore.getHighScore(0) + " moves";
        MotionBlur motionBlur = new MotionBlur(2, 3); // vairable name changed

        GameDialog dialog = new GameDialog(primaryStage, dialogTitle, dialogMessage, motionBlur);
    }

    /**
     * Show about.
     */
    private static void showAbout() {
        String title = "About this game";
        String message = "Game created by Shuguang LYU (Desmond)\n";
        GameDialog dialog = new GameDialog(primaryStage, title, message, null);
    }

    public static void showHighScore() {
        String title = "Good Job !";
        String message = "Level completed: " + GameEngine.getGameEngine().getCurrentLevel().getName() +
                "\n\n" + "High score: " + HighScore.getHighScore(GameEngine.getGameEngine().getCurrentLevel().getIndex()) + " moves\n\n"
                + "Your score: " + GameEngine.getGameEngine().getMovesCountLevel() + " moves";
        GameDialog dialog = new GameDialog(primaryStage, title, message, null);
    }

    /**
     * Toggle music.
     */
    private static void toggleMusic() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    /**
     * Toggle debug.
     */
    private static void toggleDebug() {
        GameLogger.toggleDebug();
        reloadGrid();
    }
}