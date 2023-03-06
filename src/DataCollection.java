import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import engine.core.MarioAgentEvent;
import engine.core.MarioResult;

public class DataCollection {

    public static void findPatterns(String levelName, boolean writeFiles, MarioResult result) {
        List<String> lines = new ArrayList<String>();
        List<EventRange> eventRanges = getEventRanges(result);

        calculateDistances(eventRanges);
        if (writeFiles) {
            for (int i = 0; i < eventRanges.size(); i++) {
                lines.add("" + eventRanges.get(i).getDistance() + " ( " + eventRanges.get(i).getString() + ")");
            }

            writeFile(levelName + "-distances.txt", lines);
            lines.clear();
        }

        setBoundaries(eventRanges);
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

            writeFile(levelName + "-boundaries.txt", lines);
            lines.clear();
        }

        if (writeFiles) {
            writeBoundaryTiles(levelName, eventRanges);
        }
    }

    // #region Pattern Calculation

    private static void calculateDistances(List<EventRange> events) {
        for (int i = 1; i < events.size(); i++) {
            EventRange first = events.get(i - 1);
            EventRange second = events.get(i);

            int distance = calculateDistance(first, second);
            second.setDistance(distance);
        }
    }

    private static void setBoundaries(List<EventRange> events) {
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

    private static void writeBoundaryTiles(String levelName, List<EventRange> events) {
        List<String> lines = new ArrayList<String>();

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
                lines.add("[" + startX + " .. " + endX + "]");
                foundStart = false;
                foundEnd = false;
            }
        }

        writeFile(levelName + "-positions.txt", lines);
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

    private static List<EventRange> getEventRanges(MarioResult result) {
        List<EventRange> eventRanges = new ArrayList<EventRange>();
        
        List<MarioAgentEvent> allEvents = result.getAgentEvents();
        MarioAgentEvent startEvent = allEvents.get(0);
        MarioAgentEvent currEvent;

        for (int i = 1; i < allEvents.size(); i++) {
            currEvent = allEvents.get(i);

            if (!isSameEvent(startEvent, currEvent)) {
                // The previous one was the end for this range
                MarioAgentEvent endEvent = allEvents.get(i - 1);

                EventRange range = new EventRange(startEvent, endEvent);
                eventRanges.add(range);

                // Start new range
                startEvent = currEvent;
            }

            // Make sure to record the last range of events as well
            if (i == (allEvents.size() - 1)) {
                EventRange range = new EventRange(startEvent, currEvent);
                eventRanges.add(range);
            }
        }

        return eventRanges;
    }

    private static void writeFile(String fileName, List<String> lines) {
        String folderName = "results";

        Path file = Paths.get(folderName, fileName);

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            System.out.println("Error writing file " + fileName);
        }
    }

    // #endregion
}
