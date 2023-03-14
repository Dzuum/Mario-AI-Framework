package custom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

public class LevelGenerator implements MarioLevelGenerator {
    private int patternCount;
    private String folderName = "results/lvl-1/";

    private Random rand;

    public LevelGenerator(int patternCount) {
        this.patternCount = patternCount;
    }

    /*
     * Select a random pattern file and return its lines.
     */
    private List<String> getRandomPattern() throws IOException {
        File[] listOfFiles = new File(folderName).listFiles();
        List<String> lines = Files.readAllLines(listOfFiles[rand.nextInt(listOfFiles.length)].toPath());
        return lines;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        rand = new Random();
        model.clearMap();

        // The start x index for the next pattern
        int currentMapX = 0;

        try {
            for (int i = 0; i < patternCount; i++) {
                List<String> pattern = getRandomPattern();

                // length() should be OK since Files.readAllLines seems to omit newline characters
                int patternWidth = pattern.get(0).length();

                String combinedPattern = "";
                for (int j = 0; j < pattern.size(); j++) {
                    combinedPattern += pattern.get(j) + "\n";
                }

                model.copyFromString(currentMapX, 0, 0, 0, patternWidth, model.getHeight(), combinedPattern);

                currentMapX += patternWidth;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model.getMap();
    }

    @Override
    public String getGeneratorName() {
        return "CustomPatternLevelGenerator";
    }
}
