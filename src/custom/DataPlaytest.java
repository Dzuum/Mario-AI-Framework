package custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import engine.core.MarioAgentEvent;
import engine.core.MarioResult;
import engine.helper.MarioActions;

public class DataPlaytest {

    public static void saveResult(long seed, MarioResult result) {
        Path folderPath = Paths.get(Settings.PLAYTEST_FOLDER_NAME, String.valueOf(seed));


        // 1 - MarioResult
        List<String> lines = new ArrayList<String>();
        lines.add("Game Status: " + result.getGameStatus().toString());
        lines.add("  Percentage Completion: " + result.getCompletionPercentage());
        lines.add("  Lives: " + result.getCurrentLives());
        lines.add("  Coins: " + result.getCurrentCoins());
        lines.add("  Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        lines.add("  Times Hurt: " + result.getMarioNumHurts());
        lines.add("Mario State: " + result.getMarioMode());
        lines.add("  Mushrooms Collected: " + result.getNumCollectedMushrooms());
        lines.add("  Fire Flowers Collected: " + result.getNumCollectedFireflower());
        lines.add("Total Kills: " + result.getKillsTotal());
        lines.add("  Stomps: " + result.getKillsByStomp());
        lines.add("  Fireballs: " + result.getKillsByFire());
        lines.add("  Shells: " + result.getKillsByShell());
        lines.add("  Falls: " + result.getKillsByFall());
        lines.add("Bricks Destroyed: " + result.getNumDestroyedBricks());
        lines.add("  Brick Bumps: " + result.getNumBumpBrick());
        lines.add("  Question Block Bumps: " + result.getNumBumpQuestionBlock());
        lines.add("Jumps: " + result.getNumJumps());
        lines.add("  Max X Jump: " + result.getMaxXJump());
        lines.add("  Max Air Time: " + result.getMaxJumpAirTime());

        for (int i = 1; i < 100; i++) {
            Path filePath = Paths.get(folderPath.toString(), i + "-1" + Settings.PLAYTEST_MARIORESULT_FILE + ".txt");
            File file = new File(filePath.toString());

            if (!file.exists()) {
                Utils.writeAllLines(filePath, lines);
                break;
            }
        }


        // 2 - Inputs
        List<String> uniqueInputLines = new ArrayList<String>();
        List<String> allInputLines = new ArrayList<String>();

        boolean[] prevAction = new boolean[5];
        for (int i = 0; i < prevAction.length; i++) {
            prevAction[i] = false;
        }

        String uniqueLine, allLine;
        for (int i = 0; i < result.getAgentEvents().size(); i++) {
            MarioAgentEvent event = result.getAgentEvents().get(i);
            
            uniqueLine = event.getTimeSinceStartTicks() + ":";
            allLine = event.getTimeSinceStartTicks() + ":";

            for (int actionId = 0; actionId < 5; actionId++) {
                // Check if started a new action during this event (tick)
                if (event.getActions()[actionId] && !prevAction[actionId]) {
                    uniqueLine += " " + MarioActions.getAction(actionId).getString();
                }
            
                prevAction[actionId] = event.getActions()[actionId];

                if (event.getActions()[actionId])
                    allLine += " " + MarioActions.getAction(actionId).getString();
            }

            uniqueInputLines.add(uniqueLine);
            allInputLines.add(allLine);
        }

        for (int i = 1; i < 100; i++) {
            Path filePath = Paths.get(folderPath.toString(), i + "-2a" + Settings.PLAYTEST_ALLINPUTS_FILE + ".txt");
            File file = new File(filePath.toString());

            if (!file.exists()) {
                Utils.writeAllLines(filePath, allInputLines);
                break;
            }
        }

        for (int i = 1; i < 100; i++) {
            Path filePath = Paths.get(folderPath.toString(), i + "-2b" + Settings.PLAYTEST_UNIQUEINPUTS_FILE + ".txt");
            File file = new File(filePath.toString());

            if (!file.exists()) {
                Utils.writeAllLines(filePath, uniqueInputLines);
                break;
            }
        }


        // 3 - States (serialized)
        List<State> states = DataCollection.getStates(result);
        DataCollection.calculateDistances(states);
        DataCollection.setBoundaryInfo(states);

        for (int i = 1; i < 100; i++) {
            Path filePath = Paths.get(folderPath.toString(), i + "-3" + Settings.PLAYTEST_STATE_FILE + ".txt");
            File file = new File(filePath.toString());

            if (!file.exists()) {
                try {
                    FileOutputStream fileStream = new FileOutputStream(file);
                    ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
        
                    objectStream.writeObject(states);
        
                    objectStream.close();
                    fileStream.close();
                } catch (Exception ex) {
                    System.out.println("Error serializing States list.");
                    ex.printStackTrace();
                }

                break;
            }
        }


        // 4 - States (debug)
        lines.clear();
        for (int i = 0; i < states.size(); i++) {
            State state = states.get(i);
            lines.add("#" + i + ": " + state.getDistanceGMA() + " ( " + state.getDebugInfo() + " ) ( " + state.getStateString() + " )");
        }
        
        for (int i = 1; i < 100; i++) {
            Path filePath = Paths.get(folderPath.toString(), i + "-4" + Settings.PLAYTEST_STATEDEBUG_FILE + ".txt");
            File file = new File(filePath.toString());

            if (!file.exists()) {
                Utils.writeAllLines(filePath, lines);
                break;
            }
        }
    }
}
