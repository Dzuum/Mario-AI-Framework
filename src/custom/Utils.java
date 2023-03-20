package custom;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Utils {

    private static final Map<String, String> metricsToFramework = new HashMap<String, String>() {{
        put("[0]", "-");    // Sky
        put("[a]", "o");    // Coin
        put("[g]", "X");    // Ground
        put("[r]", "#");    // Solid block
        put("[o]", "!");    // Coin block
        put("[l]", "2");    // Invisible coin block
        put("[m]", "S");    // Regular brick
        put("[w]", "@");    // Power-up brick
        put("[t]", "t");    // Pipe
        put("[Ft]", "t");   // Piranha pipe         TODO: Enemy pipe?
        put("[e]", "g");    // Goomba
        put("[v]", "g");    // Beetle               TODO: Replaced with Goomba
        put("[k]", "k");    // Green Koopa
        put("[K]", "K");    // Winged Green Koopa
        put("[d]", "r");    // Red Koopa
        put("[Q]", "-");    // Lakitu               TODO: Unsupported?
        put("[N]", "-");    // Hammer Bro           TODO: Unsupported?
        put("[ham]", "-");  // Hammer Bro           TODO: Unsupported?
        put("[c]", "B");    // Bullet Bill head
        put("[0#]", "S");   // Flag stand
        put("[[Y]]", "-");  // Trampoline           TODO: Unsupported?
        put("[Y]", "-");    // Trampoline           TODO: Unsupported?
    }};

    public static void convertLevelMetricsToFramework(String source, String target) {
        List<String> lines = readAllLines(Paths.get(source));

        // Omit first line since it contains map size
        lines.remove(0);

        for (int i = 0; i < lines.size(); i++) {
            for (Entry<String, String> entry : metricsToFramework.entrySet()) {
                String from = entry.getKey();
                String to = entry.getValue();

                lines.set(i, lines.get(i).replace(from, to));
            }
        }

        // Add empty sky block rows at the start to reach the target 16 tiles height
        int linesNeeded = 16 - lines.size();
        for (int i = 0; i < linesNeeded; i++) {
            lines.add(0, String.join("", Collections.nCopies(lines.get(0).length(), "-")));
        }

        writeAllLines(Paths.get(target), lines);
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
