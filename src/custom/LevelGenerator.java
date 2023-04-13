package custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

public class LevelGenerator implements MarioLevelGenerator {
    private int patternCount;
    private String sourceLevel = "World 1-1";

    private Random rand;
    private List<Pattern> patterns;

    public LevelGenerator(int patternCount) {
        this.patternCount = patternCount;
        patterns = new ArrayList<>();
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        loadPatterns(sourceLevel);

        rand = new Random();
        model.clearMap();

        // The start x index for the next pattern
        int currentMapX = 0;

        for (int i = 0; i < patternCount; i++) {
            Pattern pattern = patterns.get(rand.nextInt(patterns.size()));
            List<String> geometry = pattern.getGeometry();

            String combinedPattern = "";
            for (int j = 0; j < geometry.size(); j++) {
                combinedPattern += geometry.get(j) + "\n";
            }

            model.copyFromString(currentMapX, 0, 0, 0, pattern.getTileWidth(), model.getHeight(), combinedPattern);

            currentMapX += pattern.getTileWidth();
        }

        return model.getMap();
    }

    private void loadPatterns(String level) {
        try {
            Path directoryPath = Paths.get(Settings.RESULTS_FOLDER_NAME, level, Settings.PATTERNS_FOLDER_NAME);
            File directory = new File(directoryPath.toString());
            File[] directoryListing = directory.listFiles();

            for (File file : directoryListing) {
                if (file.getName().startsWith(Settings.PATTERNS_FILE_NAME)) {
                    FileInputStream fileStream = new FileInputStream(file.getPath());
                    ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                        
                    patterns.add((Pattern)objectStream.readObject());
                        
                    objectStream.close();
                    fileStream.close();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error reading patterns for level " + level);
            ex.printStackTrace();
        }
    }

    @Override
    public String getGeneratorName() {
        return "CustomPatternLevelGenerator";
    }
}
