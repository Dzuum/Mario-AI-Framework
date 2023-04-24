package custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    private static final String[][] conversionMap = new String[][]
    {
        { "[0]",    "-"},   // Sky
        { "[a]",    "o"},   // Coin
        { "[g]",    "X"},   // Ground
        { "[r]",    "#"},   // Solid block
        { "[o]",    "U"},   // Power-up block
        { "[o]",    "!"},   // ? block #1
        { "[o]",    "Q"},   // ? block #2
        { "[0]",    "1"},   // Invisible 1-Up block             // TODO: Seems unsupported in metrics
        { "[l]",    "2"},   // Invisible coin block
        { "[m]",    "S"},   // Regular brick
        { "[w]",    "@"},   // Power-up brick #1
        { "[w]",    "?"},   // Power-up brick #2
        { "[m]",    "L"},    // 1-Up brick                      // TODO: Seems unsupported in metrics
        { "[o]",    "C"},   // Coin brick
        { "[p]",    "S"},   // Moving platform (replaced with solid block in framework-originals, too)
        { "[p]",    "%"},   // Mushroom block (jump-through)    //  TODO: Seems unsupported in metrics
        { "[0]",    "|"},   // Mushroom root body               // TODO: Seems unsupported in metrics
        { "[t]",    "t"},   // Pipe
        { "[Ft]",   "T"},   // Piranha pipe                     // TODO: need special attention in both directions
        { "[Y]",    "-"},   // Trampoline                       // TODO: Unsupported?
        { "[e]",    "g"},   // Goomba #1
        { "[e]",    "E"},   // Goomba #2
        { "[v]",    "g"},   // Beetle (replaced with Goomba)
        { "[k]",    "k"},   // Green Koopa
        { "[K]",    "K"},   // Winged Green Koopa
        { "[d]",    "r"},   // Red Koopa
        { "[D]",    "R"},   // Winged Red Koopa
        { "[s]",    "y"},   // Spiky                            // TODO: Unsure if correct metrics counterpart
        { "[S]",    "Y"},   // Winged Spiky                     // TODO: Unsure if correct metrics counterpart
        { "[Q]",    "-"},   // Lakitu                           // TODO: Unsupported?
        { "[N]",    "-"},   // Hammer Bro                       // TODO: Unsupported?
        { "[ham]",  "-"},   // Hammer Bro                       // TODO: Unsupported?
        { "[c]",    "B"},   // Bullet Bill head
        { "[0]",    "b"},   // Bullet Bill body                 // TODO: metrics -> framework at least
        { "[c]",    "*"},   // Bullet Bill body                 // TODO: framework -> metrics at least; see README.md
        { "[0#]",   "S"},   // Flag stand metrics (e.g. 1-3)
        { "[g#]",   "X"},   // Flag stand metrics (e.g. 1-1)
        { "[0]",    "F"},   // Flag stand framework (e.g. 1-1)
        { "[0]",    "M"}    // Mario start framework (not used in metrics)
    };

    public static void convertLevelMetricsToFramework(String source, String target) {
        List<String> lines = readAllLines(Paths.get(source));

        // Omit first line since it contains map size
        lines.remove(0);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // At least one level had this issue
            line = line.replace("[[", "[");
            line = line.replace("]]", "]");

            for (int k = 0; k < conversionMap.length; k++) {
                String from = conversionMap[k][0];
                String to = conversionMap[k][1];

                line = line.replace(from, to);
            }

            if (line.contains("[") || line.contains("]")) {
                System.out.println("ERROR: Didn't find match for some characters in line '" + line + "'");
            }

            lines.set(i, line);
        }

        // Add empty sky block rows at the start to reach the target 16 tiles height
        int linesNeeded = 16 - lines.size();
        for (int i = 0; i < linesNeeded; i++) {
            lines.add(0, String.join("", Collections.nCopies(lines.get(0).length(), "-")));
        }

        writeAllLines(Paths.get(target), lines);
    }
    
    public static void convertLevelFrameworkToMetrics(String source, String target) {
        List<String> sourceLines = readAllLines(Paths.get(source));
        List<String> newLines = new ArrayList<String>();

        for (int lineIdx = 0; lineIdx < sourceLines.size(); lineIdx++) {
            String sourceLine = sourceLines.get(lineIdx);
            String newLine = "";

            for (int charIdx = 0; charIdx < sourceLine.length(); charIdx++) {
                String character = String.valueOf(sourceLine.charAt(charIdx));
                boolean foundConversion = false;

                for (int i = 0; i < conversionMap.length; i++) {
                    String from = conversionMap[i][1];
                    String to = conversionMap[i][0];

                    if (from.equals(character)) {
                        newLine += to;
                        foundConversion = true;
                        break;
                    }
                }

                if (!foundConversion) {
                    System.out.println("ERROR: Didn't find match for character '" + character + "'");
                }
            }

            newLines.add(newLine);
        }

        newLines.add(0, "HEIGHT=" + newLines.size() + ";WIDTH=" + sourceLines.get(0).length() + ";");

        writeAllLines(Paths.get(target), newLines);
    }

    public static void serializeStates(String levelName, List<State> states) {
        String fileName = Settings.SERIALIZED_STATE_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
        Path newPath = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, fileName);

        try {
            FileOutputStream fileStream = new FileOutputStream(new File(newPath.toString()));
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(states);

            objectStream.close();
            fileStream.close();
        } catch (Exception ex) {
            System.out.println("Error serializing States list.");
            ex.printStackTrace();
        }
    }

    public static List<State> deserializeStates(String levelName) {
        List<State> states = new ArrayList<State>();

        try {
            String fileName = Settings.SERIALIZED_STATE_FILE_NAME + Settings.RESULTS_FILE_EXTENSION;
            Path newPath = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, fileName);

            FileInputStream fileStream = new FileInputStream(newPath.toString());
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                        
            states = (List<State>)objectStream.readObject();
                        
            objectStream.close();
            fileStream.close();
        } catch (Exception ex) {
            System.out.println("Error reading states for level " + levelName);
            ex.printStackTrace();

            states.clear();
        }

        return states;
    }

    public static List<Pattern> loadPatternsForLevel(String levelName) {
        List<Pattern> patterns = new ArrayList<Pattern>();

        try {
            Path directoryPath = Paths.get(Settings.RESULTS_FOLDER_NAME, levelName, Settings.PATTERNS_FOLDER_NAME);
            File directory = new File(directoryPath.toString());
            File[] directoryListing = directory.listFiles();

            for (File file : directoryListing) {
                if (file.getName().startsWith(Settings.SERIALIZED_PATTERN_FILE_NAME)) {
                    FileInputStream fileStream = new FileInputStream(file.getPath());
                    ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                        
                    patterns.add((Pattern)objectStream.readObject());
                        
                    objectStream.close();
                    fileStream.close();
                }
            }

            // Sort by pattern index in ascending order
            patterns.sort((p1, p2) -> p1.getPatternIndex() - p2.getPatternIndex());
        } catch (Exception ex) {
            System.out.println("Error reading patterns for level " + levelName);
            ex.printStackTrace();

            patterns.clear();
        }

        return patterns;
    }

    public static List<String> readAllLines(Path path) {
        List<String> lines = new ArrayList<String>();

        try {
            lines = Files.readAllLines(path);
        } catch (Exception ex) {
            System.out.println("readLines: Error reading file for " + path.toString() + "!");
            ex.printStackTrace();
        }

        return lines;
    }

    public static void writeAllLines(Path path, List<String> lines) {
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectory(path.getParent());
            }
            
            // Write all but the last line
            Files.write(path, lines.subList(0, lines.size() - 1), StandardCharsets.UTF_8);

            // Write the last line in a way that doesn't cause a newline (i.e. an empty last row)
            Files.write(path, lines.get(lines.size() - 1).getBytes("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException ioe) {
            System.out.println("Error writing file " + path.toString());
            ioe.printStackTrace();
        }
    }
}
