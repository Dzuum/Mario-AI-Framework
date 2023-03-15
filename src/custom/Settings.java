package custom;

public class Settings {
    public enum LaunchMode { Agent, Player, Results, LevelGenerator }

    public static LaunchMode LAUNCH_MODE = LaunchMode.LevelGenerator;

    public static final String ORIGINAL_LEVELS_PATH = "./levels/original/";
    public static final String RESULTS_FOLDER_NAME = "results";

    public static final String RESULTS_FILE_EXTENSION = ".txt";
    public static final String DISTANCES_FILE_SUFFIX = "-A_distances";
    public static final String BOUNDARIES_FILE_SUFFIX = "-B_boundaries";
    public static final String TILE_RANGES_FILE_SUFFIX = "-C_tiles";
    public static final String PATTERNS_FILE_NAME = "Pattern_{i}";
}
