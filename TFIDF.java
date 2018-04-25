import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.*;

import static java.util.stream.Collectors.toMap;

public class TFIDF {
    static ArrayList<HashMap<String, Double>> list = new ArrayList<>();
    static HashMap<String, Double> idf = new HashMap<>();
    static ArrayList<HashMap<String, Double>> tfidf = new ArrayList<>();

    private static void tf(String fileName) throws IOException {
        // Declare an individual HashMap.
        HashMap<String, Double> tf = new HashMap<>();
        HashMap<String, Integer> occurrence = new HashMap<>();

        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line, str = "";
        int a = 0;
        int b = 0;
        while ((line = br.readLine()) != null) {
            str += line + " ";
            b++;
        }

        StringTokenizer st = new StringTokenizer(str);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            Integer n = occurrence.get(s);
            if (n == null) {
                occurrence.put(s, 1);
            } else {
                occurrence.put(s, n + 1);
            }
            a++;
        }

        for (Object o : occurrence.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String t = pair.getKey().toString();
            Integer n = (Integer) pair.getValue();
            Double d = (double)n / (double)a;
            tf.put(t, d);
        }

        list.add(tf);
    }

    public static void idf() {
        HashMap<String, Integer> contains = new HashMap<>();
        Integer N = list.size();

        // calculate number of documents containing term
        for (HashMap<String, Double> element : list) {
            for (Object o : element.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                String t = pair.getKey().toString();
                Integer c = contains.get(t);
                if (c == null) {
                    contains.put(t, 1);
                } else {
                    contains.put(t, c + 1);
                }
            }
        }

        // calculate idf
        for (Object o : contains.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String t = pair.getKey().toString();
            Integer n = (Integer) pair.getValue();
            if (n > 0) {
                Double df = Math.log10((double)N / (double)n);
                idf.put(t, df);
            }
        }
    }

    public static void tfidf() {
        Integer N = list.size();

        for(int i = 0; i < N; i++) {
            tfidf.add(new HashMap<>());
        }

        for (Object o : idf.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String t = pair.getKey().toString();
            Double d1 = (Double) pair.getValue();
            Integer index = 0;
            for (HashMap<String, Double> element : list) {
                HashMap<String, Double> tf = tfidf.get(index);
                Double d2 = element.get(t);
                if (d2 == null) {
                    tf.put(t, 0.0);
                } else {
                    Double dd = d1 * d2;
                    tf.put(t, dd);
                }
                index++;
            }
        }
    }

    public static void main(String args[]) throws IOException {
        for (String s: args) {
            tf(s);
        }
        idf();
        tfidf();

        System.out.println("\nMax TFIDF value for each file.\n");
        Integer index = 0;
        for (Map<String, Double> element : tfidf) {
            Map<String, Double> sorted = element
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));
            Entry<String, Double> entry = sorted.entrySet().iterator().next();
            System.out.println("=========");
            System.out.println(args[index]);
            System.out.println("=========");
            System.out.println(entry.getKey() + " = " + entry.getValue());
            index++;
        }
    }
}
