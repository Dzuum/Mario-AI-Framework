package custom;

public class Settings {
    public enum LaunchMode { Agent, Player, Results, LevelGenerator }
    public enum StateComparison { Input, State }
    public enum TimeScoring { None, Ticks, Millis }

    public static LaunchMode LAUNCH_MODE = LaunchMode.Agent;

    public static StateComparison ComparisonStrategy = StateComparison.State;
    public static boolean StateIncludeHorizontalInput = true;
    public static boolean StateIncludeAirborne = true;
    public static TimeScoring StateTimeScoring = TimeScoring.Ticks;

    public static final String ORIGINAL_LEVELS_PATH = "./levels/original/";
    public static final String RESULTS_FOLDER_NAME = "results";

    public static final String RESULTS_FILE_EXTENSION = ".txt";
    public static final String DISTANCES_FILE_SUFFIX = "-A_distances";
    public static final String BOUNDARIES_FILE_SUFFIX = "-B_boundaries";
    public static final String TILE_RANGES_FILE_SUFFIX = "-C_tiles";
    public static final String PATTERNS_FILE_NAME = "Pattern_{i}";
}
