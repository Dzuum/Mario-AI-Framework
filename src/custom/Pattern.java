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

    private float intensity;

    public Pattern(String source, int index, List<String> geometry, List<State> states) {
        this.sourceLevel = source;
        this.patternIndex = index;
        this.geometry = geometry;
        this.states = states;
        calculateIntensity();
    }

    public String getSourceLevel() { return sourceLevel; }
    public void setSourceLevel(String newLevel) { sourceLevel = newLevel; }

    public int getPatternIndex() { return patternIndex; }
    public void setPatternIndex(int newIndex) { patternIndex = newIndex; }

    public List<String> getGeometry() { return geometry; }
    public void setGeometry(List<String> newGeometry) { geometry = newGeometry; }

    public List<State> getStates() { return states; }
    public void setStates(List<State> newStates) { states = newStates; }

    public float getIntensity() { return intensity; }
    public void setIntensity(float newIntensity) { intensity = newIntensity; }

    /**
     * Intensity adjusted to pattern tile width.
     */
    public float getAdjustedIntensity() {
        return (intensity * 10.0f / getTileWidth());
    }

    public int getTileWidth() {
        return geometry.get(0).length();
    }

    public int getStartTileX() {
        return states.get(0).getStartTileX();
    }

    public int getEndTileX() {
        return states.get(states.size() - 1).getEndTileX();
    }

    public void serialize() {
        String fileName = Settings.SERIALIZED_PATTERN_FILE_NAME + patternIndex + Settings.RESULTS_FILE_EXTENSION;
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

    public void writeGeometryFile() {
        String prefix = "Geometry";

        String fileName = prefix + Settings.SERIALIZED_PATTERN_FILE_NAME + patternIndex + Settings.RESULTS_FILE_EXTENSION;
        Path newPath = Paths.get(Settings.RESULTS_FOLDER_NAME, sourceLevel, Settings.PATTERNS_FOLDER_NAME, fileName);

        Utils.writeAllLines(newPath, geometry);
    }

    private void calculateIntensity() {
        intensity = 0;

        boolean[] prevActions = new boolean[5];
        for (int i = 0; i < states.size(); i++) {
            boolean[] actions = states.get(i).getActions();

            for (int k = 0; k < prevActions.length; k++) {
                // Add intensity for each new input event
                if (actions[k] && !prevActions[k]) {
                    intensity++;
                }

                prevActions[k] = actions[k];
            }
        }
    }
}
