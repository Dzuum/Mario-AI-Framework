import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import engine.core.MarioGame;
import engine.core.MarioResult;

import custom.DataCollection;
import custom.DataCollection.LaunchMode;

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

        String levelName = "lvl-1";

        if (DataCollection.LAUNCH_MODE == LaunchMode.Agent) {
            DataCollection.findPatterns(levelName, true,
                game.runGame(new agents.robinBaumgarten.Agent(),
                             getLevel("./levels/original/" + levelName + ".txt"),
                             20, 0, true));
        } else if (DataCollection.LAUNCH_MODE == LaunchMode.Player) {
            printResults(game.runGame(new agents.human.Agent(),
                             getLevel("./levels/original/" + levelName + ".txt"),
                             200, 0, true));
        } else if (DataCollection.LAUNCH_MODE == LaunchMode.Results) {
            LinkedHashMap<Integer, Integer> gestalts = DataCollection.loadGestaltPatterns(levelName);
            game.viewResults(gestalts, getLevel("./levels/original/" + levelName + ".txt"), 200, 0, true);
        }
    }
}
