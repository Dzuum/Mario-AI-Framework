package custom;

public class Settings {

    public enum LaunchMode
    {
        Agent, Player, Results, LevelGenerator,
        ConvertMetricsToFramework, ConvertFrameworkToMetrics
    }

    public enum TimeScoring { Ticks, Millis }

    public static LaunchMode LAUNCH_MODE = LaunchMode.Agent;

    public static TimeScoring StateTimeScoring = TimeScoring.Millis;
    public static int StateCutoffMillis = 50; 
    public static int StateCutoffTicks = 3; 

    // #region Global

    public static final String LEVEL_NAME = "World 1-1";
    public static final String ORIGINAL_LEVELS_PATH = "./levels/custom/framework-originals/";

    public static final boolean WRITE_FILES = true;
    public static final String RESULTS_FOLDER_NAME = "results";
    public static final String RESULTS_FILE_EXTENSION = ".txt";

    // #endregion

    // #region Conversion

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
    public static final String PATTERNS_FILE_NAME = "Pattern_";

    // #endregion
}
