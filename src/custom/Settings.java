package custom;

public class Settings {

    public enum LaunchMode { Agent, Player, Results, LevelGenerator, LevelConversion }

    public static LaunchMode LAUNCH_MODE = LaunchMode.Agent;

    // #region Global

    public static final String LEVEL_NAME = "World 1-1";
    public static final String ORIGINAL_LEVELS_PATH = "./levels/smb/";

    public static final boolean WRITE_FILES = false;
    public static final String RESULTS_FOLDER_NAME = "results";
    public static final String RESULTS_FILE_EXTENSION = ".txt";

    // #endregion

    // #region Conversion

    public static final String CONVERT_FROM = "levels/smb/w 3-2.txt";
    public static final String CONVERT_TO = "levels/smb/World 3-2.txt";

    // #endregion

    // #region Input Analysis

    public static final String INPUTS_FILE_SUFFIX = "-GraphSource-InputIntensity";

    // #endregion

    // #region State Analysis

    public static final String DISTANCES_FILE_SUFFIX = "-A_distances";
    public static final String BOUNDARIES_FILE_SUFFIX = "-B_boundaries";
    public static final String TILE_RANGES_FILE_SUFFIX = "-C_tiles";
    public static final String PATTERNS_FILE_NAME = "Pattern_{i}";

    // #endregion
}
