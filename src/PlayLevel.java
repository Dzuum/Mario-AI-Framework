import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import engine.core.MarioGame;
import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioResult;
import engine.core.MarioTimer;
import custom.DataAnalysis;
import custom.DataCollection;
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

            DataCollection.recordInputs(Settings.LEVEL_NAME, result);
            DataCollection.recordStates(Settings.LEVEL_NAME, result);
            DataCollection.extractPatterns(Settings.LEVEL_NAME);

        } else if (Settings.LAUNCH_MODE == LaunchMode.Player) {

            printResults(game.runGame(new agents.human.Agent(),
                             getLevel(Settings.ORIGINAL_LEVELS_PATH + Settings.LEVEL_NAME + ".txt"),
                             200, 0, true));

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

        }
    }
}
