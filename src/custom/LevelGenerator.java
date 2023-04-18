package custom;

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
        patterns = Utils.loadPatternsForLevel(sourceLevel);

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

    @Override
    public String getGeneratorName() {
        return "CustomPatternLevelGenerator";
    }
}
