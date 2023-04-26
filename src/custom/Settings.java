package custom;

import java.util.HashMap;

public class Settings {

    public enum LaunchMode
    {
        Agent, Player, Results, LevelGenerator,
        ConvertMetricsToFramework, ConvertFrameworkToMetrics
    }

    public enum TimeScoring { Ticks, Millis }

    // #region Global

    public static LaunchMode LAUNCH_MODE = LaunchMode.Agent;

    public static final String LEVEL_NAME = "World 1-2";
    public static final String ORIGINAL_LEVELS_PATH = "./levels/custom/framework-selected/";

    public static final boolean WRITE_FILES = true;
    public static final String RESULTS_FOLDER_NAME = "results";
    public static final String RESULTS_FILE_EXTENSION = ".txt";

    public static HashMap<String, Boolean> UseFlowerEnemyFix = new HashMap<String, Boolean>()
    {{
        put("World 1-1", false);
        put("World 1-2", false);
        put("World 2-1", false);
        put("World 3-1", false);
        put("World 3-2", false);
        put("World 4-1", false);
        put("World 4-2", false);
        put("World 5-1", false);
        put("World 5-2", false);
        put("World 6-1", false);
        put("World 6-2", false);
        put("World 7-1", false);
        put("World 8-1", false);
        put("World 8-2", false);
        put("World 8-3", false);
    }};


    // #endregion

    // #region GMA

    public static TimeScoring StateTimeScoring = TimeScoring.Ticks;

    public static HashMap<String, Integer> StateMinimumTicks = new HashMap<String, Integer>()
    {{
        put("World 1-1", 5);
        put("World 1-2", 6);
        put("World 2-1", 5);
        put("World 3-1", 6);
        put("World 3-2", 5);
        put("World 4-1", 6);
        put("World 4-2", 6);
        put("World 5-1", 5);
        put("World 5-2", 6);
        put("World 6-1", 6);
        put("World 6-2", 5);
        put("World 7-1", 5);
        put("World 8-1", 5);
        put("World 8-2", 5);
        put("World 8-3", 5);
    }};

    /** Pagnutti 0.75 */
    public static double WeightDirection = 0.25;
    /** Pagnutti 1.00 */
    public static double WeightPowerup = 1;
    /** Pagnutti 0.25 */
    public static double WeightGroundState = 0.25;
    /** Pagnutti 0.50 */
    public static double WeightAirborneState = 0.5;
    /** Pagnutti 0.01 */
    public static double WeightTime = 0.1;

    // #endregion

    // #region Files -- Conversion

    public static final String CONVERT_METRICS_SOURCE = "levels/custom/metrics-originals/";
    public static final String CONVERT_METRICS_TARGET = "levels/custom/metrics-converted/";
    public static final String CONVERT_METRICS_FILE = "SMB-w1-l1_final.txt";

    public static final String CONVERT_FRAMEWORK_SOURCE = "levels/custom/framework-originals/";
    public static final String CONVERT_FRAMEWORK_TARGET = "levels/custom/framework-converted/";
    public static final String CONVERT_FRAMEWORK_FILE = "World 1-1.txt";

    // #endregion

    // #region Files -- Input Analysis

    public static final String INPUTS_FOLDER_NAME = "inputs";
    public static final String UNIQUE_INPUTS_FILE_SUFFIX = "-GraphSource-UniqueInputs";
    public static final String ALL_INPUTS_FILE_SUFFIX = "-GraphSource-AllInputs";

    // #endregion

    // #region Files -- State Analysis

    public static final String PATTERNS_FOLDER_NAME = "patterns";
    public static final String DISTANCES_FILE_NAME = "1 - Distances";
    public static final String STATES_FILE_NAME = "2 - States";
    public static final String TILE_RANGES_FILE_NAME = "3 - Tiles";
    public static final String SERIALIZED_STATE_FILE_NAME = "States";
    public static final String SERIALIZED_PATTERN_FILE_NAME = "Pattern_";
    public static final String INTENSITY_FILE_NAME = "PatternIntensity";

    // #endregion
}
