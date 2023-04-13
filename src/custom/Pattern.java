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

    public Pattern(String source, int index, List<String> geometry) {
        this.sourceLevel = source;
        this.patternIndex = index;
        this.geometry = geometry;
    }

    public List<String> getGeometry() { return geometry; }
    public int getTileWidth() { return geometry.get(0).length(); }

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
