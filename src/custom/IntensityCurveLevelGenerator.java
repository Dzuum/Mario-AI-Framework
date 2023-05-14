package custom;

import java.util.List;
import java.util.Map;
import java.util.Random;

import engine.core.MarioLevelGenerator;
import engine.core.MarioLevelModel;
import engine.core.MarioTimer;

public class IntensityCurveLevelGenerator implements MarioLevelGenerator {
    private static final boolean debug = true;

    private String compareLevel;
    private long seed = 1338;
    private Random rand = new Random(seed);

    private List<Pattern> allPatterns;
    private List<Pattern> comparePatterns;
    private float allowedIntensityVariance = 0.1f;

    public void initialize(long seed, String compareLevel) {
        this.seed = seed;
        rand = new Random(this.seed);

        this.compareLevel = compareLevel;
    }

    @Override
    public String getGeneratedLevel(MarioLevelModel model, MarioTimer timer) {
        if (debug)
            System.out.println("Generating level with seed " + seed);

        // Create big enough level to hold all possible levels
        model = new MarioLevelModel(1000, 16);

        allPatterns = Utils.loadAllPatterns();

        Map<String, List<Pattern>> groupedPatterns = Utils.loadAllPatternsGrouped();
        comparePatterns = groupedPatterns.get(compareLevel);

        int patternCount = getPatternCount();

        model.clearMap();

        String startPattern =
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "------\n" +
        "XXXXXX\n" +
        "XXXXXX\n";

        // The start x index for the next pattern
        int currentMapX = 0;

        model.copyFromString(currentMapX, 0, 0, 0, 6, 16, startPattern);
        currentMapX += 6;

        for (int i = 0; i < patternCount; i++) {
            Pattern pattern = selectPattern(i);
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
        int patternCount = comparePatterns.size();

        if (debug) {
            System.out.println("Pattern target " + patternCount);
        }

        return patternCount;
    }

    private Pattern selectPattern(int patternIndex) {
        Pattern comparePattern = comparePatterns.get(patternIndex);
        float compareIntensity = comparePattern.getAdjustedIntensity();
        float minIntensity = compareIntensity - allowedIntensityVariance;
        float maxIntensity = compareIntensity + allowedIntensityVariance;

        Pattern[] suitablePatterns = allPatterns
            .stream().filter(e -> e.getAdjustedIntensity() >= minIntensity && e.getAdjustedIntensity() <= maxIntensity)
            .toArray(Pattern[]::new);

        if (debug) {
            System.out.println("Original " + comparePattern.getSourceLevel() + " #" + comparePattern.getPatternIndex()
                + " has intensity " + comparePattern.getAdjustedIntensity()
                + " (suitable patterns: " + suitablePatterns.length + ")");

            for (Pattern suitablePattern : suitablePatterns) {
                System.out.println(suitablePattern.getSourceLevel() + " #" + suitablePattern.getPatternIndex() + ": " + suitablePattern.getAdjustedIntensity());
            }
        }

        Pattern pattern = suitablePatterns[rand.nextInt(suitablePatterns.length)];
        
        if (debug) {
            System.out.println("Selected pattern #" + pattern.getPatternIndex() + " from " + pattern.getSourceLevel()
                + " (length: " + pattern.getTileWidth() + ", intensity: " + pattern.getAdjustedIntensity() + ")");
        }

        return pattern;
    }

    @Override
    public String getGeneratorName() {
        return "IntensityCurveLevelGenerator";
    }
}
