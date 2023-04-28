package custom;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataAnalysis {

    public static class PatternInfo {
        private static final DecimalFormat df = new DecimalFormat("0.00");
        
        public int Count;

        public int MinLength;
        public int MaxLength;

        public double LengthAverage;
        public double LengthSD1;
        public double LengthSD2;
        public double LengthPercentageWithinSD1;
        public double LengthPercentageWithinSD2;
        
        public void print() {
            System.out.println("####################################");
            System.out.println("############# Patterns #############");
            System.out.println("Patterns: " + Count);
            System.out.println("Shortest: " + MinLength + " tiles");
            System.out.println("Longest: " + MaxLength + " tiles");
            System.out.println();
            System.out.println("Length Avg: " + df.format(LengthAverage) + " tiles");
            System.out.println("Length SD1: " 
                + df.format(LengthSD1) + " (" + df.format(LengthPercentageWithinSD1) + " % within SD1)");
            System.out.println("Length SD2: " 
                + df.format(LengthSD2) + " (" + df.format(LengthPercentageWithinSD2) + " % within SD2)");
            System.out.println();
        }
    }

    public static class LevelInfo {
        private static final DecimalFormat df = new DecimalFormat("0.00");

        public int MinLength;
        public int MaxLength;

        public double TileLengthAverage;
        public double TileLengthSD1;
        public double TileLengthSD2;
        public double TileLengthPercentageWithinSD1;
        public double TileLengthPercentageWithinSD2;

        public double PatternCountAverage;
        public double PatternCountSD1;
        public double PatternCountSD2;
        public double PatternCountPercentageWithinSD1;
        public double PatternCountPercentageWithinSD2;
        
        public void print() {
            System.out.println("####################################");
            System.out.println("############## Levels ##############");
            System.out.println("Shortest: " + MinLength + " tiles");
            System.out.println("Longest: " + MaxLength + " tiles");
            System.out.println();
            System.out.println("Length Avg: " + df.format(TileLengthAverage) + " tiles");
            System.out.println("Length SD1: " 
                + df.format(TileLengthSD1) + " (" + df.format(TileLengthPercentageWithinSD1) + " % within SD1)");
            System.out.println("Length SD2: " 
                + df.format(TileLengthSD2) + " (" + df.format(TileLengthPercentageWithinSD2) + " % within SD2)");
            System.out.println();
            System.out.println("Pattern Count Avg: " + df.format(PatternCountAverage));
            System.out.println("Pattern Count SD1: " 
                + df.format(PatternCountSD1) + " (" + df.format(PatternCountPercentageWithinSD1) + " % within SD1)");
            System.out.println("Pattern Count SD2: " 
                + df.format(PatternCountSD2) + " (" + df.format(PatternCountPercentageWithinSD2) + " % within SD2)");
            System.out.println();
        }
    }

    public static void analyzeAllPatterns() {
        List<Pattern> allPatterns = Utils.loadAllPatterns();
        Map<String, List<Pattern>> groupedPatterns = allPatterns
            .stream()
            .collect(
                Collectors.groupingBy(Pattern::getSourceLevel));

        PatternInfo patternInfo = analyzePatterns(allPatterns);
        LevelInfo levelInfo = analyzeLevels(groupedPatterns);

        patternInfo.print();
        levelInfo.print();
    }

    public static PatternInfo analyzePatterns(List<Pattern> allPatterns) {
        PatternInfo info = new PatternInfo();
        info.Count = allPatterns.size();
        
        // Min / Max
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < allPatterns.size(); i++) {
            Pattern pattern = allPatterns.get(i);

            if (pattern.getTileWidth() < min)
                min = pattern.getTileWidth();

            if (pattern.getTileWidth() > max)
                max = pattern.getTileWidth();
        }

        info.MinLength = min;
        info.MaxLength = max;


        // Average
        double average = 0;

        for (int i = 0; i < allPatterns.size(); i++)
            average += allPatterns.get(i).getTileWidth();

        average = average / allPatterns.size();
        info.LengthAverage = average;
        

        // Standard Deviation 1
        double standardDeviation = 0;

        for (int i = 0; i < allPatterns.size(); i++) {
            standardDeviation += Math.pow(allPatterns.get(i).getTileWidth() - average, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / allPatterns.size());
        info.LengthSD1 = standardDeviation;

        // % within standard deviation 1
        double percentage = 0;

        for (int i = 0; i < allPatterns.size(); i++) {
            int patternLength = allPatterns.get(i).getTileWidth();
            if (patternLength > average - info.LengthSD1 && patternLength < average + info.LengthSD1)
                percentage++;
        }

        percentage = percentage / allPatterns.size();
        info.LengthPercentageWithinSD1 = percentage * 100;


        // Standard Deviation 2 (tile length)
        info.LengthSD2 = info.LengthSD1 * 2;
        
        // % within standard deviation 2
        percentage = 0;

        for (int i = 0; i < allPatterns.size(); i++) {
            int patternLength = allPatterns.get(i).getTileWidth();
            if (patternLength > average - info.LengthSD2 && patternLength < average + info.LengthSD2)
                percentage++;
        }

        percentage = percentage / allPatterns.size();
        info.LengthPercentageWithinSD2 = percentage * 100;


        return info;
    }

    public static LevelInfo analyzeLevels(Map<String, List<Pattern>> groupedPatterns) {
        LevelInfo info = new LevelInfo();


        // Min / Max
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int mapWidth = 0;
            for (int i = 0; i < kvp.getValue().size(); i++) {
                mapWidth += kvp.getValue().get(i).getTileWidth();
            }

            if (mapWidth < min)
                min = mapWidth;
            if (mapWidth > max)
                max = mapWidth;
        }

        info.MinLength = min;
        info.MaxLength = max;


        // Average (tile length)
        double average = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            for (int i = 0; i < kvp.getValue().size(); i++) {
                average += kvp.getValue().get(i).getTileWidth();
            }
        }

        average = average / groupedPatterns.size();
        info.TileLengthAverage = average;


        // Standard Deviation 1 (tile length)
        double standardDeviation = 0;
        
        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int mapWidth = 0;
            for (int i = 0; i < kvp.getValue().size(); i++) {
                mapWidth += kvp.getValue().get(i).getTileWidth();
            }

            standardDeviation += Math.pow(mapWidth - average, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / groupedPatterns.size());
        info.TileLengthSD1 = standardDeviation;

        // % within standard deviation 1
        double percentage = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int mapWidth = 0;
            for (int i = 0; i < kvp.getValue().size(); i++) {
                mapWidth += kvp.getValue().get(i).getTileWidth();
            }

            if (mapWidth > average - info.TileLengthSD1 && mapWidth < average + info.TileLengthSD1)
                percentage++;
        }

        percentage = percentage / groupedPatterns.size();
        info.TileLengthPercentageWithinSD1 = percentage * 100;



        // Standard Deviation 2 (tile length)
        info.TileLengthSD2 = info.TileLengthSD1 * 2;
        
        // % within standard deviation 2
        percentage = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int mapWidth = 0;
            for (int i = 0; i < kvp.getValue().size(); i++) {
                mapWidth += kvp.getValue().get(i).getTileWidth();
            }

            if (mapWidth > average - info.TileLengthSD2 && mapWidth < average + info.TileLengthSD2)
                percentage++;
        }

        percentage = percentage / groupedPatterns.size();
        info.TileLengthPercentageWithinSD2 = percentage * 100;



        // Average (pattern count)
        average = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            average += kvp.getValue().size();
        }

        average = average / groupedPatterns.size();
        info.PatternCountAverage = average;


        // Standard Deviation 1 (pattern count)
        standardDeviation = 0;
        
        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            standardDeviation += Math.pow(kvp.getValue().size() - average, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / groupedPatterns.size());
        info.PatternCountSD1 = standardDeviation;
        
        // % within standard deviation 1 (pattern count)
        percentage = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int patternCount = kvp.getValue().size();
            if (patternCount > average - info.PatternCountSD1 && patternCount < average + info.PatternCountSD1)
                percentage++;
        }

        percentage = percentage / groupedPatterns.size();
        info.PatternCountPercentageWithinSD1 = percentage * 100;


        // Standard Deviation 2 (pattern count)
        info.PatternCountSD2 = info.PatternCountSD1 * 2;
        
        // % within standard deviation 2 (pattern count)
        percentage = 0;

        for (Map.Entry<String, List<Pattern>> kvp : groupedPatterns.entrySet()) {
            int patternCount = kvp.getValue().size();
            if (patternCount > average - info.PatternCountSD2 && patternCount < average + info.PatternCountSD2)
                percentage++;
        }

        percentage = percentage / groupedPatterns.size();
        info.PatternCountPercentageWithinSD2 = percentage * 100;


        return info;
    }
}
