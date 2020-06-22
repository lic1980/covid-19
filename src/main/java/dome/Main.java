package dome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
    private static final String OUTPUT_FORMAT = "First-digit '%s' occupy %2.1f%%";
    
    private static final float D1 = 0.301f;
    private static final float D2 = 0.176f;
    private static final float D3 = 0.125f;
    private static final float D4 = 0.097f;
    private static final float D5 = 0.079f;
    private static final float D6 = 0.067f;
    private static final float D7 = 0.058f;
    private static final float D8 = 0.051f;
    private static final float D9 = 0.046f;

    
    private static final Map<Integer, Float> reference = new HashMap<>();

    static {
        reference.put(1, D1);
        reference.put(2, D2);
        reference.put(3, D3);
        reference.put(4, D4);
        reference.put(5, D5);
        reference.put(6, D6);
        reference.put(7, D7);
        reference.put(8, D8);
        reference.put(9, D9);
    }


    Map<Integer, Integer> dis = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.exe(args[0]);
    }

    public void exe(String dirPath) throws IOException {
        File dir = new File(dirPath);
        float total = 0f;
        for (File f : dir.listFiles()) {
            total = total + goThrough(dis, f);
        }

        Map<Integer, Float> actual = new HashMap<>();
        for (Entry<Integer, Integer> e : dis.entrySet()) {
            System.out.println(String.format(OUTPUT_FORMAT, e.getKey(), e.getValue()/ total * 100));
            actual.put(e.getKey(), e.getValue()/ total);
        }
        
        System.out.println("Offset is " + getOffset(actual));
    }

    private int goThrough(Map<Integer, Integer> dis, File f) throws IOException {
        int total = 0;
        String record;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            while ((record = reader.readLine()) != null) {
                String[] fields = record.split(",");
                String field = fields[5];
                if (isTitleLine(field)) {
                    Integer key = getFirstChar(field);
                    if (key != 0) {
                        total++;
                        if (dis.containsKey(key)) {
                            Integer value = dis.get(key);
                            dis.put(key, value + 1);
                        } else {
                            dis.put(key, 1);
                        }
                    }
                }

            }
        }
        return total;
    }

    private boolean isTitleLine(String field) {
        return field.matches("^[0-9]*$");
    }

    private Integer getFirstChar(String str) {
        return Integer.valueOf(str.substring(0, 1));
    }

    private float getOffset(Map<Integer, Float> results) {
        double offset = 0f;
        for (Entry<Integer, Float> result : results.entrySet()) {
            offset = offset + Math.pow((reference.get(result.getKey()) - result.getValue()), 2);
        }
        return (float) Math.sqrt(offset / 10);
    }
}
