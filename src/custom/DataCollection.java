package custom;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import engine.core.MarioAgentEvent;
import engine.core.MarioAgentEvent.GroundState;
import engine.core.MarioAgentEvent.MovementDirection;
import engine.core.MarioResult;
import engine.helper.MarioActions;

import custom.Settings.TimeScoring;

public class DataCollection {

    // #region Input Analysis

    public static void recordInputs(String levelName, MarioResult result) {
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

        if (Settings.WRITE_FILES) {
            String fileName = levelName + Settings.UNIQUE_INPUTS_FILE_SUFFIX + Settings.RESULTS_FILE_EXTENSION;
            Path path = Paths.get(Settings.RESULTS_FOLDER_NAME, Settings.INPUTS_FOLDER_NAME, fileName);
            Utils.writeAllLines(path, uniqueInputLines);
            
            fileName = levelName + Settings.ALL_INPUTS_FILE_SUFFIX + Settings.RESULTS_FILE_EXTENSION;
            path = Paths.get(Settings.RESULTS_FOLDER_NAME, Settings.INPUTS_FOLDER_NAME, fileName);
            Utils.writeAllLines(path, allInputLines);
        }
    }

    // #endregion

    public static void findPatterns(String levelName, MarioResult result) {
        List<State> states = getStates(result);
        
        // Combine states that are too short to the previous ones
        for (int i = 1; i < states.size(); i++) {
            int durationTicks = states.get(i).getDurationTicks();
            long durationMillis = states.get(i).getDurationMillis();

            if ((Settings.StateTimeScoring == TimeScoring.Millis && durationMillis < Settings.StateCutoffMillis) ||
                (Settings.StateTimeScoring == TimeScoring.Ticks && durationTicks < Settings.StateCutoffTicks)) {
                // Combine with previous state
                states.get(i - 1).addDuration(durationTicks, durationMillis);
                states.remove(i);
                i--;
            }
        }



        calculateDistances(states);
        setBoundaryInfo(states);
        List<Pattern> patterns = createPatterns(levelName, states);

        if (Settings.WRITE_FILES) {
            emptyPatternResults(levelName);
            for (Pattern pattern : patterns) {
                pattern.serialize();
                pattern.writeDebugPatternFile();
            }

            logResultsToFiles(levelName);
        }
    }

    private static void logResultsToFiles(String levelName) {
        List<Pattern> patterns = Utils.loadPatternsForLevel(levelName);
        List<String> lines = new ArrayList<String>();

        /*****************
         * 1 - Distances */
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);

            for (int k = 0; k < pattern.getStates().size(); k++) {
                State state = pattern.getStates().get(k);
                lines.add("" + state.getDistanceGMA() + " ( " + state.getStateString() + " )");
            }
        }

        String fileName = Settings.DISTANCES_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
        Path path = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, fileName);
        Utils.writeAllLines(path, lines);
        
        /**************
         * 2 - States */
        lines.clear();

        int index = 0;
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            lines.add("[" + index + " .. " + (index + pattern.getStates().size() - 1) + "] (event count: " + pattern.getStates().size() + ")");
            index += pattern.getStates().size();
        }

        fileName = Settings.STATES_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
        path = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, fileName);
        Utils.writeAllLines(path, lines);

        /*************
         * 3 - Tiles */
        lines.clear();

        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            lines.add("[" + pattern.getStartTileX() + " .. " + pattern.getEndTileX() + "]");
        }

        fileName = Settings.TILE_RANGES_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
        path = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, fileName);
        Utils.writeAllLines(path, lines);

        /*****************
         * 4 - Intensity */
        lines.clear();

        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            lines.add("" + pattern.getIntensity());
        }

        fileName = levelName + "-" + Settings.INTENSITY_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
        path = Paths.get(Settings.RESULTS_FOLDER_NAME, Settings.INPUTS_FOLDER_NAME, fileName);
        Utils.writeAllLines(path, lines);
    }

    // #region Pattern Calculation

    /**
     * Join identical sequential events into one state.
     */
    private static List<State> getStates(MarioResult result) {
        List<State> states = new ArrayList<State>();
        
        List<MarioAgentEvent> allEvents = result.getAgentEvents();

        int startIndex = 0;
        MarioAgentEvent startEvent = allEvents.get(0);
        MarioAgentEvent currEvent;

        for (int i = 1; i < allEvents.size(); i++) {
            currEvent = allEvents.get(i);

            if (!isSameState(startEvent, currEvent)) {
                // The previous one was the end for this range
                MarioAgentEvent endEvent = allEvents.get(i - 1);

                State state = new State(startEvent, endEvent);
                state.setAgentEvents(new ArrayList<>(allEvents.subList(startIndex, i)));
                states.add(state);

                // Start new range
                startEvent = currEvent;
                startIndex = i;
            }

            // Make sure to record the last range of events as well
            if (i == (allEvents.size() - 1)) {
                State state = new State(startEvent, currEvent);
                state.setAgentEvents(new ArrayList<>(allEvents.subList(startIndex, i)));
                states.add(state);
            }
        }

        return states;
    }

    /**
     * Calculate the distances to previous state for each entry.
     */
    private static void calculateDistances(List<State> states) {
        for (int i = 1; i < states.size(); i++) {
            State first = states.get(i - 1);
            State second = states.get(i);

            double distance = calculateDistance(first, second);
            second.setDistanceGMA(distance);
        }
    }

    /**
     * Determine the gestalt boundaries based on the distances.
     */
    private static void setBoundaryInfo(List<State> states) {
        states.get(0).setStartBoundary();

        for (int i = 1; i < states.size() - 1; i++) {
            double prev = states.get(i - 1).getDistanceGMA();
            double curr = states.get(i).getDistanceGMA();
            double next = states.get(i + 1).getDistanceGMA();

            // If distance to previous is higher than the adjacent two,
            // then this is the start of a new boundary
            if (curr > prev && curr > next) {
                states.get(i - 1).setEndBoundary();
                states.get(i).setStartBoundary();
            }
        }

        states.get(states.size() - 1).setEndBoundary();
    }

    /**
     * Empty the results from previous run.
     */
    private static void emptyPatternResults(String levelName) {
        try {
            Path folderPath = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, Settings.PATTERNS_FOLDER_NAME);

            if (Files.exists(folderPath)) {
                try (DirectoryStream<Path> entries = Files.newDirectoryStream(folderPath)) {
                    for (Path entry : entries) {
                        Files.delete(entry);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("emptyResultFolder: Error deleting existing directory!");
            ex.printStackTrace();
        }
    }

    /**
     * Write the found patterns as text files. 
     */
    private static List<Pattern> createPatterns(String levelName, List<State> allStates) {
        Path originalLevelPath = Paths.get(Settings.ORIGINAL_LEVELS_PATH, levelName + ".txt");
        List<String> levelLines = Utils.readAllLines(originalLevelPath);

        int patternIndex = 0;
        List<Pattern> patterns = new ArrayList<Pattern>();
        List<String> geometry = new ArrayList<String>();

        int startIndex = -1, endIndex;
        State state;
        for (int i = 0; i < allStates.size(); i++) {
            state = allStates.get(i);

            if (state.isStartBoundary()) {
                startIndex = i;
            }

            if (state.isEndBoundary()) {
                endIndex = i;

                int startTileX = allStates.get(startIndex).getStartTileX();
                int endTileX = allStates.get(endIndex).getEndTileX();

                geometry.clear();

                for (int k = 0; k < levelLines.size(); k++) {
                    // The end is exclusive so add 1
                    String line = levelLines.get(k).substring(startTileX, endTileX + 1);
    
                    // Remove start point; otherwise the start point may be at the middle of the level
                    line = line.replace('M', '-');
    
                    // Remove the goal; otherwise the goal may be at the middle of the level
                    line = line.replace('F', '-');
    
                    geometry.add(line);
                }
    
                // Sublist end index is exclusive so add 1
                // Also, the lists are copied using the constructor
                patterns.add(new Pattern(levelName, patternIndex,
                    new ArrayList<>(geometry),
                    new ArrayList<>(allStates.subList(startIndex, endIndex + 1))));

                patternIndex += 1;

                // Reset back to undefined
                startIndex = -1;
            }
        }

        return patterns;
    }

    // #endregion

    // #region GMA

    private static boolean isSameState(MarioAgentEvent event, MarioAgentEvent otherEvent) {
        // 1. Movement Direction
        if (event.getMovementDirection() != otherEvent.getMovementDirection())
            return false;

        // 2. Powerup State
        if (event.getMarioState() != otherEvent.getMarioState())
            return false;
        
        // 3. Ground State
        if (event.getGroundState() != otherEvent.getGroundState())
            return false;

        // 4. Airborne State
        if (event.getMarioOnGround() != otherEvent.getMarioOnGround())
            return false;

        return true;
    }
 
    private static double calculateDistance(EventRange first, EventRange second) {
        // Pagnutti 0.75
        double weightDirection = 0.75;
        // Pagnutti 1
        double weightPowerup = 1;
        // Pagnutti 0.25
        double weightGroundState = 0.25;
        // Pagnutti 0.5
        double weightAirborneState = 0.5;
        // Pagnutti 0.01
        double weightTime = 0.01;

        double scoreSum = 0;

        // 1. Movement Direction
        MovementDirection directionPrev = first.getMovementDirection();
        MovementDirection directionNext = second.getMovementDirection();
        if (directionPrev != directionNext) {
            double scoreAdd = 0;

            if (directionPrev == MovementDirection.None || directionNext == MovementDirection.None) {
                // Left <-> None or Right <-> None
                // Already verified the directions are different
                scoreAdd = 1;
            } else {
                // Going from left to right or vice versa
                scoreAdd = 2;
            }

            scoreAdd = Math.pow(weightDirection * scoreAdd, 2);
            scoreSum += scoreAdd;
        }

        // 2. Powerup State
        int powerupPrev = first.getMarioState();
        int powerupNext = second.getMarioState();
        if (powerupPrev != powerupNext) {
            double scoreAdd = 0;

            if (powerupPrev == 1 || powerupNext == 1) {
                // Small <-> Big or Big <-> Fire
                // Already verified the powerup stat4es are different
                scoreAdd = 1;
            } else {
                // Going from small to fire or vice versa
                scoreAdd = 2;
            }

            scoreAdd = Math.pow(weightPowerup * scoreAdd, 2);
            scoreSum += scoreAdd;
        }
        
        // 3. Ground State
        GroundState groundPrev = first.getGroundState();
        GroundState groundNext = second.getGroundState();
        if (groundPrev != groundNext) {
            double scoreAdd = 0;

            if (groundPrev == GroundState.None || groundNext == GroundState.None) {
                // Already verified the ground states are different
                scoreAdd = 1;
            } else {
                // Going from crouching to running or vice versa
                scoreAdd = 2;
            }

            scoreAdd = Math.pow(weightGroundState * scoreAdd, 2);
            scoreSum += scoreAdd;
        }

        // 4. Airborne State
        boolean airbornePrev = first.getMarioOnGround();
        boolean airborneNext = second.getMarioOnGround();
        if (airbornePrev != airborneNext) {
            double scoreAdd = 1;
            scoreAdd = Math.pow(weightAirborneState * scoreAdd, 2);
            scoreSum += scoreAdd;
        }
        
        // 5. Time
        long duration = 0;

        if (Settings.StateTimeScoring == TimeScoring.Millis)
            duration = first.getDurationMillis();
        else
            duration = first.getDurationTicks();
        
        scoreSum += Math.pow(weightTime * duration, 2);

        // Finalisation
        scoreSum = Math.sqrt(scoreSum);
        long score = Math.round(scoreSum * 10);
        return score;
    }

    // #endregion
}
