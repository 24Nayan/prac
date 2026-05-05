import java.io.*;
import java.util.*;

public class Assembler {

    static class Symbol {
        String name;
        int address;
        boolean defined = false;
        boolean used = false;

        Symbol(String name) {
            this.name = name;
        }
    }

    static class Literal {
        String value;
        int address = -1;

        Literal(String value) {
            this.value = value;
        }
    }

    static Map<String, Integer> MOT = new HashMap<>();
    static Map<String, Integer> REG = new HashMap<>();

    static LinkedHashMap<String, Symbol> symtab = new LinkedHashMap<>();
    static ArrayList<Literal> littab = new ArrayList<>();
    static ArrayList<Integer> pooltab = new ArrayList<>();
    static ArrayList<String> intermediate = new ArrayList<>();

    static int LC = 0;
    static int lineNo = 0;

    public static void main(String[] args) throws Exception {

        initializeTables();
        pass1();
        pass2();
    }

    static void initializeTables() {

        MOT.put("STOP", 0);
        MOT.put("ADD", 1);
        MOT.put("SUB", 2);
        MOT.put("MOVER", 4);
        MOT.put("MOVEM", 5);

        REG.put("AREG", 1);
        REG.put("BREG", 2);
        REG.put("CREG", 3);
    }

    // ================= PASS 1 =================
    static void pass1() throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        String line;

        pooltab.add(0); // first pool

        System.out.println("=========== PASS 1 ===========");

        while ((line = br.readLine()) != null) {

            lineNo++;
            line = line.trim();
            if (line.isEmpty()) continue;

            String parts[] = line.split("\\s+");

            if (parts[0].equals("START")) {
                LC = Integer.parseInt(parts[1]);
                intermediate.add("(AD,01) (C," + LC + ")");
                continue;
            }

            if (parts[0].equals("END")) {
                assignLiterals();
                intermediate.add("(AD,02)");
                continue;
            }

            if (parts[0].equals("LTORG")) {
                assignLiterals();
                pooltab.add(littab.size());
                intermediate.add("(AD,03)");
                continue;
            }

            // DC
            if (parts.length > 1 && parts[1].equals("DC")) {

                if (symtab.containsKey(parts[0]) && symtab.get(parts[0]).defined) {
                    System.out.println("Error (Line " + lineNo + "): Duplicate symbol " + parts[0]);
                    continue;
                }

                Symbol s = symtab.getOrDefault(parts[0], new Symbol(parts[0]));
                s.address = LC;
                s.defined = true;
                symtab.put(parts[0], s);

                intermediate.add("(DL,01) (C," + parts[2] + ")");
                LC++;
                continue;
            }

            // Imperative statement
            if (!MOT.containsKey(parts[0])) {
                System.out.println("Error (Line " + lineNo + "): Invalid mnemonic " + parts[0]);
                continue;
            }

            String ic = "(IS," + String.format("%02d", MOT.get(parts[0])) + ") ";

            for (int i = 1; i < parts.length; i++) {

                String operand = parts[i];

                if (REG.containsKey(operand)) {
                    ic += "(RG," + REG.get(operand) + ") ";
                }
                else if (operand.startsWith("='")) {
                    littab.add(new Literal(operand));
                    ic += "(L," + littab.size() + ") ";
                }
                else {
                    Symbol s = symtab.getOrDefault(operand, new Symbol(operand));
                    s.used = true;
                    symtab.put(operand, s);
                    ic += "(S," + symtab.size() + ") ";
                }
            }

            intermediate.add(ic);
            LC++;
        }

        br.close();

        printTables();
    }

    static void assignLiterals() {
        for (Literal l : littab) {
            if (l.address == -1) {
                l.address = LC++;
            }
        }
    }

    static void printTables() {

        System.out.println("\nSYMBOL TABLE:");
        for (Symbol s : symtab.values()) {
            System.out.println(s.name + "  ->  " + s.address);
        }

        System.out.println("\nLITERAL TABLE:");
        for (Literal l : littab) {
            System.out.println(l.value + "  ->  " + l.address);
        }

        System.out.println("\nPOOL TABLE:");
        for (int i = 0; i < pooltab.size(); i++) {
            System.out.println("Pool " + i + " -> Literal Index " + pooltab.get(i));
        }

        System.out.println("\nINTERMEDIATE CODE:");
        for (String s : intermediate) {
            System.out.println(s);
        }

        // Error: symbol not declared
        for (Symbol s : symtab.values()) {
            if (s.used && !s.defined) {
                System.out.println("Error: Symbol not declared -> " + s.name);
            }
        }

        // Warning: declared but not used
        for (Symbol s : symtab.values()) {
            if (s.defined && !s.used) {
                System.out.println("Warning: Symbol declared but not used -> " + s.name);
            }
        }
    }

    // ================= PASS 2 =================
    static void pass2() {

        System.out.println("\n=========== PASS 2 ===========");
    
        int lc = 100;   // start value from START
    
        for (String line : intermediate) {
    
            if (line.startsWith("(IS")) {
    
                String[] parts = line.split("\\) ");
    
                // Extract opcode
                String opcode = parts[0].substring(4, 6);
    
                int reg = 0;
                int memAddr = 0;
    
                for (int i = 1; i < parts.length; i++) {
    
                    if (parts[i].startsWith("(RG")) {
                        reg = Integer.parseInt(parts[i].substring(4, 5));
                    }
    
                    else if (parts[i].startsWith("(S")) {
                        int index = Integer.parseInt(parts[i].substring(3, 4)) - 1;
                        memAddr = new ArrayList<>(symtab.values()).get(index).address;
                    }
    
                    else if (parts[i].startsWith("(L")) {
                        int index = Integer.parseInt(parts[i].substring(3, 4)) - 1;
                        memAddr = littab.get(index).address;
                    }
                }
    
                System.out.println(lc + "   " + opcode + "   " + reg + "   " + memAddr);
                lc++;
            }
        }
    }
}