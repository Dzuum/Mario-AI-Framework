package custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Pattern implements Serializable {
    // Name of the original level
    private String sourceLevel;
    // Index of the patterns created from source
    private int patternIndex;
    // The actual pattern tiles in framework format
    private List<String> geometry;
    // The states that form this pattern. These in turn contain all the events the AI created.
    private List<State> states;

    public Pattern(String source, int index, List<String> geometry, List<State> states) {
        this.sourceLevel = source;
        this.patternIndex = index;
        this.geometry = geometry;
        this.states = states;
    }

    public List<String> getGeometry() { return geometry; }
    public List<State> getStates() { return states; }

    public int getTileWidth() { return geometry.get(0).length(); }

    public int calculateIntensity() {
        return 0;
    }

    public int getStartTileX() {
        return states.get(0).getStartTileX();
    }

    public int getEndTileX() {
        return states.get(states.size() - 1).getEndTileX();
    }

    public void serialize() {
        String fileName = Settings.PATTERNS_FILE_NAME + patternIndex + Settings.RESULTS_FILE_EXTENSION;
        Path newPath = Paths.get(Settings.RESULTS_FOLDER_NAME, sourceLevel, Settings.PATTERNS_FOLDER_NAME, fileName);

        try {
            FileOutputStream fileStream = new FileOutputStream(new File(newPath.toString()));
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(this);

            objectStream.close();
            fileStream.close();
        } catch (Exception ex) {
            System.out.println("Error serializing Pattern.");
            ex.printStackTrace();
        }
    }

    public void writeDebugPatternFile() {
        String debugPrefix = "debug";

        String fileName = debugPrefix + Settings.PATTERNS_FILE_NAME + patternIndex + Settings.RESULTS_FILE_EXTENSION;
        Path newPath = Paths.get(Settings.RESULTS_FOLDER_NAME, sourceLevel, Settings.PATTERNS_FOLDER_NAME, fileName);

        Utils.writeAllLines(newPath, geometry);
    }
}
