import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import engine.core.MarioGame;
import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioResult;
import engine.core.MarioTimer;
import engine.helper.GameStatus;

import custom.DataAnalysis;
import custom.DataCollection;
import custom.DataPlaytest;
import custom.Pattern;
import custom.Settings;
import custom.Settings.LaunchMode;
import custom.Utils;

public class PlayLevel {
    public static void printResults(MarioResult result) {
        System.out.println("****************************************************************");
        System.out.println("Game Status: " + result.getGameStatus().toString() +
                " Percentage Completion: " + result.getCompletionPercentage());
        System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() +
                " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        System.out.println("Mario State: " + result.getMarioMode() +
                " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() +
                " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() +
                " Falls: " + result.getKillsByFall() + ")");
        System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() +
                " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        System.out.println("****************************************************************");
    }

    public static String getLevel(String filepath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
        }
        return content;
    }

    public static void main(String[] args) {
        MarioGame game = new MarioGame();

        if (Settings.LAUNCH_MODE == LaunchMode.Agent) {

            MarioResult result = game.runGame(
                new agents.robinBaumgarten.Agent(),
                getLevel(Settings.ORIGINAL_LEVELS_PATH + Settings.LEVEL_NAME + ".txt"),
                20, 0, true);

            printResults(result);
            DataCollection.recordInputs(Settings.LEVEL_NAME, result);
            DataCollection.recordStates(Settings.LEVEL_NAME, result);
            DataCollection.extractPatterns(Settings.LEVEL_NAME);

        } else if (Settings.LAUNCH_MODE == LaunchMode.Player) {

            printResults(game.runGame(new agents.human.Agent(),
                             getLevel(Settings.ORIGINAL_LEVELS_PATH + Settings.LEVEL_NAME + ".txt"),
                             120, 0, true, 30, 3));

        } else if (Settings.LAUNCH_MODE == LaunchMode.Results) {

            // Load each the patterns' tile start and end positions
            List<Pattern> patterns = Utils.loadPatternsForLevel(Settings.LEVEL_NAME);
            LinkedHashMap<Integer, Integer> gestalts = new LinkedHashMap<Integer, Integer>();
            for (int i = 0; i < patterns.size(); i++) {
                Pattern pattern = patterns.get(i);
                gestalts.put(pattern.getStartTileX(), pattern.getEndTileX());
            }

            game.viewResults(gestalts, getLevel(Settings.ORIGINAL_LEVELS_PATH + Settings.LEVEL_NAME + ".txt"), 200, 0, true);

        } else if (Settings.LAUNCH_MODE == LaunchMode.LevelGenerator) {

            MarioLevelGenerator generator = new custom.LevelGenerator();
            String level = generator.getGeneratedLevel(new MarioLevelModel(1, 1), new MarioTimer(5 * 60 * 60 * 1000));
            
            printResults(game.runGame(new agents.robinBaumgarten.Agent(), level, 20, 0, true));

        } else if (Settings.LAUNCH_MODE == LaunchMode.ConvertMetricsToFramework) {

            Utils.convertLevelMetricsToFramework(
                Settings.CONVERT_METRICS_SOURCE + Settings.CONVERT_METRICS_FILE,
                Settings.CONVERT_FRAMEWORK_TARGET + Settings.CONVERT_FRAMEWORK_FILE);
            
        } else if (Settings.LAUNCH_MODE == LaunchMode.ConvertFrameworkToMetrics) {
        
            Utils.convertLevelFrameworkToMetrics(
                Settings.CONVERT_FRAMEWORK_SOURCE + Settings.CONVERT_FRAMEWORK_FILE,
                Settings.CONVERT_METRICS_TARGET + Settings.CONVERT_METRICS_FILE);

        } else if (Settings.LAUNCH_MODE == LaunchMode.UpdateGeometry) {

            Utils.applyModifiedPatternGeometries(Settings.LEVEL_NAME);

        } else if (Settings.LAUNCH_MODE == LaunchMode.AnalyzePatterns) {

            DataAnalysis.analyzeAllPatterns();

        } else if (Settings.LAUNCH_MODE == LaunchMode.GeneratePlaytest) {
            
            boolean saveResult = true;
            int testerId = 0;
            long seed = 87842897;
            String compareLevel = "World 1-1";
            // String compareLevel = "World 3-2";
            // String compareLevel = "World 5-2";

            String generatedId = "1A"; // "1A", "1B", "2A", ...
            String originalId = "1B"; // "1A", "1B", "2A", ...

            MarioLevelGenerator generator = new custom.IntensityCurveLevelGenerator();
            ((custom.IntensityCurveLevelGenerator)generator).initialize(seed, compareLevel);
            String level = generator.getGeneratedLevel(new MarioLevelModel(1, 1), new MarioTimer(5 * 60 * 60 * 1000));

            if (saveResult) {
                // Generate the playtest folder
                Path playtestFolder = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId));
                try {
                    if (!Files.exists(playtestFolder)) {
                        Files.createDirectory(playtestFolder);
                    }
                } catch (Exception ex) { }

                // Save the generated level
                String fileName = generatedId + Settings.RESULTS_FILE_EXTENSION;
                Path generatedDestination = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_LEVELS_FOLDER, fileName);
                Utils.writeAllLines(generatedDestination, Arrays.asList(level.split("\n")));

                // Save the original level
                Path originalSource = Paths.get(Settings.ORIGINAL_LEVELS_PATH, compareLevel + ".txt");
                List<String> originalLevel = Utils.readAllLines(originalSource);

                fileName = originalId + Settings.RESULTS_FILE_EXTENSION;
                Path originalDestination = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_LEVELS_FOLDER, fileName);
                Utils.writeAllLines(originalDestination, originalLevel);
            }

            // MarioResult result = game.runGame(new agents.human.Agent(), level, 120, 0, true, 30, 3);
            // MarioResult result = game.runGame(new agents.robinBaumgarten.Agent(), level, 120, 0, true, 30, 3);

        } else if (Settings.LAUNCH_MODE == LaunchMode.Practice) {

            int testerId = 0;
            String levelId = Settings.PLAYTEST_PRACTICE_LEVEL_FILE;

            Path loadPath = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_LEVELS_FOLDER, levelId + ".txt");
            String level = getLevel(loadPath.toString());

            try {
                Path path = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_INSTRUCTIONS_FILE + ".txt");
                File folder = new File(path.toString());

                Desktop desktop = Desktop.getDesktop();
                desktop.open(folder);
            } catch (Exception ex) { }

            game.runGame(new agents.human.Agent(), level, 600, 0, true, 30, 3);

        } else if (Settings.LAUNCH_MODE == LaunchMode.Playtest) {
            
            int testerId = 0;
            String levelId = "1A";
            // String levelId = "1B";
            // String levelId = "2A";
            // String levelId = "2B";
            // String levelId = "3A";
            // String levelId = "3B";

            Path loadPath = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_LEVELS_FOLDER, levelId + ".txt");
            String level = getLevel(loadPath.toString());

            MarioResult result = game.runGame(new agents.human.Agent(), level, 120, 0, true, 30, 3);
            DataPlaytest.saveResult(testerId, levelId, result);

            if (result.getGameStatus() == GameStatus.WIN) {

                try {
                    Path path = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(testerId), Settings.PLAYTEST_QUESTIONNAIRES_FOLDER);
                    File folder = new File(path.toString());

                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(folder);
                } catch (Exception ex) { }
            }

        }
    }
}
