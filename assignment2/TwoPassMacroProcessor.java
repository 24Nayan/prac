import java.io.*;
import java.util.*;

class MNTEntry {
    String name;
    int paramCount;
    int mdtIndex;

    MNTEntry(String name, int paramCount, int mdtIndex) {
        this.name = name;
        this.paramCount = paramCount;
        this.mdtIndex = mdtIndex;
    }
}

public class TwoPassMacroProcessor {

    static List<MNTEntry> MNT = new ArrayList<>();
    static List<String> MDT = new ArrayList<>();
    static Map<String, Integer> ALA = new HashMap<>();

    public static void main(String[] args) throws Exception {

        pass1("input.txt", "intermediate.txt");
        pass2("intermediate.txt", "expanded.txt");

        printMNT();
        printMDT();
    }

    // ================= PASS 1 =================
    static void pass1(String input, String intermediate) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(intermediate));

        String line;
        boolean inMacro = false;

        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (line.startsWith("MACRO")) {

    String header = line.substring(5).trim();  // remove "MACRO"
    String[] parts = header.split("\\s+|,");

    String macroName = parts[0];
    ALA.clear();

    int paramCount = 0;

    for (int i = 1; i < parts.length; i++) {
        if (!parts[i].isEmpty()) {
            paramCount++;
            ALA.put(parts[i], paramCount);
        }
    }

    MNT.add(new MNTEntry(macroName, paramCount, MDT.size()));

    while (!(line = br.readLine().trim()).equalsIgnoreCase("MEND")) {

        for (String key : ALA.keySet()) {
            line = line.replaceAll("\\b" + key + "\\b", "#" + ALA.get(key));
        }

        MDT.add(line);
    }

    MDT.add("MEND");
}
            else {
                bw.write(line);
                bw.newLine();
            }
        }

        br.close();
        bw.close();
    }

    // ================= PASS 2 =================
    static void pass2(String intermediate, String expanded) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(intermediate));
        BufferedWriter bw = new BufferedWriter(new FileWriter(expanded));

        String line;

        while ((line = br.readLine()) != null) {

            line = line.trim();
            boolean macroFound = false;

            for (MNTEntry entry : MNT) {

                if (line.startsWith(entry.name)) {

                    macroFound = true;

                    String[] parts = line.split("\\s+|,");
                    Map<Integer, String> actualALA = new HashMap<>();

                    for (int i = 1; i < parts.length; i++) {
                        actualALA.put(i, parts[i]);
                    }

                    int mdtPointer = entry.mdtIndex;

                    while (!MDT.get(mdtPointer).equals("MEND")) {

                        String mdtLine = MDT.get(mdtPointer);

                        for (Integer key : actualALA.keySet()) {
                            mdtLine = mdtLine.replace("#" + key, actualALA.get(key));
                        }

                        bw.write(mdtLine);
                        bw.newLine();
                        mdtPointer++;
                    }
                }
            }

            if (!macroFound) {
                bw.write(line);
                bw.newLine();
            }
        }

        br.close();
        bw.close();
    }

    // ================= PRINT TABLES =================
    static void printMNT() {
        System.out.println("\n===== MNT =====");
        System.out.println("Name\tParams\tMDT Index");
        for (MNTEntry e : MNT) {
            System.out.println(e.name + "\t" + e.paramCount + "\t" + e.mdtIndex);
        }
    }

    static void printMDT() {
        System.out.println("\n===== MDT =====");
        for (int i = 0; i < MDT.size(); i++) {
            System.out.println(i + " -> " + MDT.get(i));
        }
    }
}