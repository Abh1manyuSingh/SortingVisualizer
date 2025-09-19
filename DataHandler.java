import java.io.*;
import java.util.*;

public class DataHandler {
    public static int[] loadCSV(String filename) {
        List<Integer> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = br.readLine()) != null) {
                for(String s : line.split(",")) {
                    list.add(Integer.parseInt(s.trim()));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list.stream().mapToInt(i -> i).toArray();
    }
}