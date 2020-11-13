package com.ae2dms;

import javafx.scene.input.KeyCode;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The type Game engine.
 */
public class GameEngine {
    // transfer public fields to private
    private static final String GAME_NAME = "Sokoban";
    private static GameLogger logger;
    private static boolean debug = false;
    private int movesCount = 0;
    private String mapSetName;
    private Level currentLevel;
    private List<Level> levels;
    private boolean gameComplete = false;

    /**
     * Instantiates a new Game engine.
     *
     * @param input      the input
     * @param production the production
     */
    public GameEngine(InputStream input, boolean production) {
        try {
            logger = new GameLogger();
            levels = loadGameFile(input);
            currentLevel = getNextLevel();
        } catch (IOException e) {
            System.out.println("Cannot create logger.");
        } catch (NoSuchElementException e) {
            logger.warning("Cannot load the default save file: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Is debug active boolean.
     *
     * @return the boolean
     */
    public static boolean isDebugActive() {
        return debug;
    }

    /**
     * Handle key.
     *
     * @param code the code
     */
    public void handleKey(KeyCode code) {
        // switch replaced with enhanced switch
        switch (code) {
            case UP -> move(new Point(-1, 0));

            case RIGHT -> move(new Point(0, 1));

            case DOWN -> move(new Point(1, 0));

            case LEFT -> move(new Point(0, -1));

            default -> {}
                // TODO: implement something funny.
        }

        if (isDebugActive()) {
            System.out.println(code);
        }
    }

    /**
     * Move.
     *
     * @param delta the delta
     */
    private void move(Point delta) {
        if (isGameComplete()) {
            return;
        }

        Point keeperPosition = currentLevel.getKeeperPosition();
        GameObject keeper = currentLevel.objectsGrid.getGameObjectAt(keeperPosition);
        Point targetObjectPoint = GameGrid.translatePoint(keeperPosition, delta);
        GameObject keeperTarget = currentLevel.objectsGrid.getGameObjectAt(targetObjectPoint);

        if (GameEngine.isDebugActive()) {
            System.out.println("Current level state:");
            System.out.println(currentLevel.toString());
            System.out.println("Keeper pos: " + keeperPosition);
            System.out.println("Movement source obj: " + keeper);
            System.out.printf("Target object: %s at [%s]", keeperTarget, targetObjectPoint);
        }

        // method extracted
        moveKeeper(delta, keeperPosition, keeper, targetObjectPoint, keeperTarget);
    }

    private void moveKeeper(Point delta, Point keeperPosition, GameObject keeper, Point targetObjectPoint, GameObject keeperTarget) {
        boolean keeperMoved = false;

        switch (keeperTarget) {

            case WALL:
                break;

            case CRATE:

                GameObject crateTarget = currentLevel.getTargetObject(targetObjectPoint, delta);
                if (crateTarget != GameObject.FLOOR) {
                    break;
                }

                currentLevel.objectsGrid.putGameObjectAt(currentLevel.objectsGrid.getGameObjectAt(GameGrid.translatePoint(targetObjectPoint, delta)), targetObjectPoint);
                currentLevel.objectsGrid.putGameObjectAt(keeperTarget, GameGrid.translatePoint(targetObjectPoint, delta));
                currentLevel.objectsGrid.putGameObjectAt(currentLevel.objectsGrid.getGameObjectAt(GameGrid.translatePoint(keeperPosition, delta)), keeperPosition);
                currentLevel.objectsGrid.putGameObjectAt(keeper, GameGrid.translatePoint(keeperPosition, delta));
                keeperMoved = true;
                break;

            case FLOOR:
                currentLevel.objectsGrid.putGameObjectAt(currentLevel.objectsGrid.getGameObjectAt(GameGrid.translatePoint(keeperPosition, delta)), keeperPosition);
                currentLevel.objectsGrid.putGameObjectAt(keeper, GameGrid.translatePoint(keeperPosition, delta));
                keeperMoved = true;
                break;

            default:
                logger.severe("The object to be moved was not a recognised GameObject.");
                throw new AssertionError("This should not have happened. Report this problem to the developer.");
        }

        if (keeperMoved) {
            keeperPosition.translate((int) delta.getX(), (int) delta.getY());
            movesCount++;
            if (currentLevel.isComplete()) {
                if (isDebugActive()) {
                    System.out.println("Level complete!");
                }

                currentLevel = getNextLevel();
            }
        }
    }

    /**
     * Load game file list.
     *
     * @param input the input
     * @return the list
     */
    private List<Level> loadGameFile(InputStream input) {
        List<Level> levels = new ArrayList<>(5);
        int levelIndex = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            boolean parsedFirstLevel = false;
            List<String> rawLevel = new ArrayList<>();
            String levelName = "";

            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    if (rawLevel.size() != 0) {
                        Level parsedLevel = new Level(levelName, ++levelIndex, rawLevel);
                        levels.add(parsedLevel);
                    }
                    break;
                }

                if (line.contains("MapSetName")) {
                    mapSetName = line.replace("MapSetName: ", "");
                    continue;
                }

                if (line.contains("LevelName")) {
                    if (parsedFirstLevel) {
                        Level parsedLevel = new Level(levelName, ++levelIndex, rawLevel);
                        levels.add(parsedLevel);
                        rawLevel.clear();
                    } else {
                        parsedFirstLevel = true;
                    }

                    levelName = line.replace("LevelName: ", "");
                    continue;
                }

                line = line.trim();
                line = line.toUpperCase();
                if (line.matches(".*W.*W.*")) {
                    rawLevel.add(line);
                }
            }

        } catch (IOException e) {
            logger.severe("Error trying to load the game file: " + e);
        } catch (NullPointerException e) {
            logger.severe("Cannot open the requested file: " + e);
        }

        return levels;
    }

    /**
     * Is game complete boolean.
     *
     * @return the boolean
     */
    public boolean isGameComplete() {
        return gameComplete;
    }

    /**
     * Gets next level.
     *
     * @return the next level
     */
    public Level getNextLevel() {
        if (currentLevel == null) {
            return levels.get(0);
        }
        int currentLevelIndex = currentLevel.getIndex();
        if (currentLevelIndex < levels.size()) {
            return levels.get(currentLevelIndex + 1);
        }
        gameComplete = true;
        return null;
    }

    /**
     * Toggle debug.
     */
    public void toggleDebug() {
        debug = !debug;
    }

    /**
     * Gets current level.
     *
     * @return the current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    public static String getGameName() {
        return GAME_NAME;
    }

    public static GameLogger getLogger() {
        return logger;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public String getMapSetName() {
        return mapSetName;
    }

    public List<Level> getLevels() {
        return levels;
    }
}