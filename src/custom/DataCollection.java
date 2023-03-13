package custom;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import engine.core.MarioAgentEvent;
import engine.core.MarioResult;

public class DataCollection {
    public enum LaunchMode { Agent, Player, Results }

    public static LaunchMode LAUNCH_MODE = LaunchMode.Results;

    public static final String ORIGINAL_LEVELS_PATH = "./levels/original/";
    public static final String RESULTS_FOLDER_NAME = "results";

    public static final String RESULTS_FILE_EXTENSION = ".txt";
    public static final String DISTANCES_FILE_SUFFIX = "-A_distances";
    public static final String BOUNDARIES_FILE_SUFFIX = "-B_boundaries";
    public static final String TILE_RANGES_FILE_SUFFIX = "-C_tiles";
    public static final String PATTERNS_FILE_SUFFIX = "_pattern_{i}";

    public static void findPatterns(String levelName, boolean writeFiles, MarioResult result) {
        List<String> lines = new ArrayList<String>();
        List<EventRange> eventRanges = getStates(result);

        calculateDistances(eventRanges);
        if (writeFiles) {
            for (int i = 0; i < eventRanges.size(); i++) {
                lines.add("" + eventRanges.get(i).getDistance() + " ( " + eventRanges.get(i).getString() + ")");
            }

            writeResultsFile(levelName + DISTANCES_FILE_SUFFIX + RESULTS_FILE_EXTENSION, lines);
            lines.clear();
        }

        determineGestaltBoundaries(eventRanges);
        if (writeFiles) {
            String line = "";

            for (int i = 0; i < eventRanges.size(); i++) {
                if (eventRanges.get(i).isStartBoundary()) {
                    line = "[" + i + " .. ";
                }

                if (eventRanges.get(i).isEndBoundary()) {
                    line += i + "]";
                    lines.add(line);
                }
            }

            writeResultsFile(levelName + BOUNDARIES_FILE_SUFFIX + RESULTS_FILE_EXTENSION, lines);
            lines.clear();
        }

        LinkedHashMap<Integer, Integer> patterns = getGestaltTileRanges(levelName, eventRanges);
        if (writeFiles) {
            for (Entry<Integer, Integer> entry : patterns.entrySet()){    
                lines.add("[" + entry.getKey() + " .. " + entry.getValue() + "]");
            }

            writeResultsFile(levelName + TILE_RANGES_FILE_SUFFIX + RESULTS_FILE_EXTENSION, lines);
        }

        if (writeFiles) {
            writeGestaltPatterns(levelName, patterns);
        }
    }

    // #region Pattern Calculation

    /**
     * Join identical sequential events into one state.
     */
    private static List<EventRange> getStates(MarioResult result) {
        List<EventRange> states = new ArrayList<EventRange>();
        
        List<MarioAgentEvent> allEvents = result.getAgentEvents();
        MarioAgentEvent startEvent = allEvents.get(0);
        MarioAgentEvent currEvent;

        for (int i = 1; i < allEvents.size(); i++) {
            currEvent = allEvents.get(i);

            if (!isSameEvent(startEvent, currEvent)) {
                // The previous one was the end for this range
                MarioAgentEvent endEvent = allEvents.get(i - 1);

                EventRange state = new EventRange(startEvent, endEvent);
                states.add(state);

                // Start new range
                startEvent = currEvent;
            }

            // Make sure to record the last range of events as well
            if (i == (allEvents.size() - 1)) {
                EventRange state = new EventRange(startEvent, currEvent);
                states.add(state);
            }
        }

        return states;
    }

    /**
     * Calculate the distances to previous state for each entry.
     */
    private static void calculateDistances(List<EventRange> states) {
        for (int i = 1; i < states.size(); i++) {
            EventRange first = states.get(i - 1);
            EventRange second = states.get(i);

            int distance = calculateDistance(first, second);
            second.setDistance(distance);
        }
    }

    /**
     * Determine the gestalt boundaries based on the distances.
     */
    private static void determineGestaltBoundaries(List<EventRange> events) {
        events.get(0).setStartBoundary();

        for (int i = 1; i < events.size() - 1; i++) {
            int prev = events.get(i - 1).getDistance();
            int curr = events.get(i).getDistance();
            int next = events.get(i + 1).getDistance();

            // If distance to previous is higher than the adjacent two,
            // then this is the start of a new boundary
            if (curr > prev && curr > next) {
                events.get(i - 1).setEndBoundary();
                events.get(i).setStartBoundary();
            }
        }

        events.get(events.size() - 1).setEndBoundary();
    }

    private static LinkedHashMap<Integer, Integer> getGestaltTileRanges(String levelName, List<EventRange> events) {
        LinkedHashMap<Integer, Integer> gestalts = new LinkedHashMap<Integer, Integer>();

        int startX = 0, endX = 0;
        boolean foundStart = false, foundEnd = false;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).isStartBoundary()) {
                startX = (int)events.get(i).getMarioX();
                foundStart = true;
            }

            if (events.get(i).isEndBoundary()) {
                endX = (int)events.get(i).getEndX();
                foundEnd = true;
            }

            if (foundStart && foundEnd) {
                if (gestalts.containsKey(startX)) {
                    System.out.println("getGestaltTileRanges: Warning! Pattern start '" + startX + "' already exists!");
                }

                gestalts.put(startX, endX);

                foundStart = false;
                foundEnd = false;
            }
        }

        return gestalts;
    }

    /**
     * Write the found patterns as text files. 
     */
    private static void writeGestaltPatterns(String levelName, LinkedHashMap<Integer, Integer> patterns) {
        Path path = Paths.get(ORIGINAL_LEVELS_PATH, levelName + ".txt");
        List<String> levelLines = readAllLines(path);

        int patternIndex = 0;
        List<String> result = new ArrayList<String>();

        for (Entry<Integer, Integer> entry : patterns.entrySet()) {
            result.clear();
            int start = entry.getKey();
            int end = entry.getValue();

            for (int i = 0; i < levelLines.size(); i++) {
                // The end is exclusive so add 1
                result.add(levelLines.get(i).substring(start, end + 1));
            }

            // Write each pattern to their own file
            String suffix = PATTERNS_FILE_SUFFIX.replace("{i}", "" + patternIndex);
            writeResultsFile(levelName + suffix + RESULTS_FILE_EXTENSION, result);
            patternIndex += 1;
        }
    }

    // #endregion

    // #region GMA

    private static boolean isSameEvent(MarioAgentEvent event, MarioAgentEvent otherEvent) {
        return Arrays.equals(event.getActions(), otherEvent.getActions());
    }

    private static int calculateDistance(EventRange first, EventRange second) {
        int score = 0;

        // **************
        // ** MOVEMENT **
        if (first.hasHorizontalInput() != second.hasHorizontalInput()) {
            // Changing between movement and no movement
            score += 1;
        } else if (
            (first.isMovingLeft() && second.isMovingRight()) ||
            (first.isMovingRight() && second.isMovingLeft())) {
            // Changing direction
            score += 2;
        }

        // ********************
        // ** AIRBORNE STATE **
        if (first.getMarioOnGround() != second.getMarioOnGround()) {
            score += 1;
        }

        // **********
        // ** TIME **
        // score += first.getTimeMillis() * 0.01f;
        score += first.getTimeTicks() * 0.1f;

        return score;
    }

    // #endregion

    // #region Utility Functions

    public static LinkedHashMap<Integer, Integer> loadGestaltTileRanges(String levelName) {
        // Load
        Path path = Paths.get(DataCollection.RESULTS_FOLDER_NAME, levelName + TILE_RANGES_FILE_SUFFIX + RESULTS_FILE_EXTENSION);
        List<String> lines = readAllLines(path);

        // Parse
        LinkedHashMap<Integer, Integer> gestalts = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = line.replace("[", "");
            line = line.replace("]", "");

            String[] split = line.split(" .. ");
            int startTile = Integer.parseInt(split[0]);
            int endTile = Integer.parseInt(split[1]);

            gestalts.put(startTile, endTile);
        }

        return gestalts;
    }

    private static List<String> readAllLines(Path path) {
        List<String> lines = new ArrayList<String>();

        try {
            lines = Files.readAllLines(path);
        } catch (Exception ex) {
            System.out.println("readLines: Error reading file for " + path.toString() + "!");
        }

        return lines;
    }

    private static void writeResultsFile(String fileName, List<String> lines) {
        Path file = Paths.get(RESULTS_FOLDER_NAME, fileName);

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            System.out.println("Error writing file " + fileName);
        }
    }

    // #endregion
}
