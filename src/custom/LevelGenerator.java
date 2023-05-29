package custom;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

import custom.DataAnalysis.LevelInfo;

public class LevelGenerator implements MarioLevelGenerator {
    private static final boolean debug = true;

    private long seed = 1337;
    private Random rand = new Random(seed);

    public void initialize(long seed) {
        this.seed = seed;
        rand = new Random(this.seed);
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        // Create big enough level to hold all possible levels
        model = new MarioLevelModel(1000, 16);

        List<Pattern> patterns = Utils.loadAllPatterns();
        int patternCount = getPatternCount();

        model.clearMap();

        // The start x index for the next pattern
        int currentMapX = 0;

        for (int i = 0; i < patternCount; i++) {
            Pattern pattern = patterns.get(rand.nextInt(patterns.size()));

            if (debug)
                System.out.println("Selected pattern #" + pattern.getPatternIndex() + " from " + pattern.getSourceLevel() + " (length: " + pattern.getTileWidth() + ")");

            List<String> geometry = pattern.getGeometry();

            String combinedPattern = "";
            for (int j = 0; j < geometry.size(); j++) {
                combinedPattern += geometry.get(j) + "\n";
            }

            model.copyFromString(currentMapX, 0, 0, 0, pattern.getTileWidth(), model.getHeight(), combinedPattern);
            currentMapX += pattern.getTileWidth();
        }

        // Resize to correct size
        String storedMap = model.getMap();
        model = new MarioLevelModel(currentMapX, 16);
        model.copyFromString(storedMap);

        if (debug)
            System.out.println("Generated map tile width: " + model.getWidth() + " " + currentMapX);

        return model.getMap();
    }

    private int getPatternCount() {
        LevelInfo info = DataAnalysis.analyzeLevels();

        // Pattern count is within +- two standard deviations from the average
        double min = info.PatternCountAverage - info.PatternCountSD2;
        double max = info.PatternCountAverage + info.PatternCountSD2;

        double target = min + rand.nextDouble() * (max - min);
        int patternCount = (int)Math.round(target);

        if (debug) {
            DecimalFormat df = new DecimalFormat("0.00");
            System.out.println("Pattern target " + df.format(target) + " -> " + patternCount + " (min: " + df.format(min) + ", max: " + df.format(max) + ")");
        }

        return patternCount;
    }

    @Override
    public String getGeneratorName() {
        return "CustomPatternLevelGenerator";
    }
}
