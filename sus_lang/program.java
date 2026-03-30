import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Lexer {
    private Map<String, Map<String, String>> tokens = Map.of(
        "KEYWORD", Map.of(
            "log", "LOG",
            "mov", "MOVE",
            "turn", "TURN",
            "wait", "WAIT",
            "add", "ADD",
            "sud", "SUB",
            "mul", "MUL",
            "div", "DIV"
        ),
        "OPERATOR", Map.of(
            "+", "PLUS",
            "-", "MINUS",
            "*", "MULTIPLY",
            "/", "DIVIDE",
            "=", "EQUALS",
            "|", "PIPE"
        )
    );
    ArrayList<ArrayList<ArrayList<String>>> tokenList = new ArrayList<>();

    public boolean inKeywords(String token) {
        return tokens.get("KEYWORD").containsKey(token);
    }

    public boolean inOperator(String token) {
        return tokens.get("OPERATOR").containsKey(token);
    }

    public ArrayList<ArrayList<ArrayList<String>>> tokenize(ArrayList<String> lines) {
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] tokenArray = line.split(" ");
            ArrayList<ArrayList<String>> lineTokens = new ArrayList<>();

            for (String token : tokenArray) {
                if (token.isEmpty()) continue;
                ArrayList<String> t = new ArrayList<>();
                if (inKeywords(token)) {
                    t.add(this.tokens.get("KEYWORD").get(token));
                    t.add(token);
                } else if (inOperator(token)) {
                    t.add(this.tokens.get("OPERATOR").get(token));
                    t.add(token);
                } else if (token.matches("-?\\d+(\\.\\d+)?")) {
                    t.add("NUMBER");
                    t.add(token);
                } else {
                    t.add("IDENTIFIER");
                    t.add(token);
                }
                lineTokens.add(t);
            }
            if (!lineTokens.isEmpty()) {
                tokenList.add(lineTokens);
            }
        }
        return tokenList;
    }
}

class execution {
    public HashMap<String, Double> registers = new HashMap<>();
    public HashMap<String, Double> memory = new HashMap<>();
    public HashMap<String, Double> variables = new HashMap<>();

    public void createVM(int RegSize, int MemSize) {
        for (int i = 0; i < RegSize; i++) {
            registers.put("R" + i, 0.0);
        }
        for (int i = 0; i < MemSize; i++) {
            memory.put("M" + i, 0.0);
        }
    }

    private Double getValue(ArrayList<String> token) {
        if (token.get(0).equals("NUMBER")) {
            return Double.parseDouble(token.get(1));
        } else if (registers.containsKey(token.get(1))) {
            return registers.get(token.get(1));
        } else if (memory.containsKey(token.get(1))) {
            return memory.get(token.get(1));
        } else if (variables.containsKey(token.get(1))) {
            return variables.get(token.get(1));
        }
        return null;
    }

    public void execute(ArrayList<ArrayList<ArrayList<String>>> tokens) {
        for (ArrayList<ArrayList<String>> line : tokens) {
            if (line.isEmpty()) continue;
            String command = line.get(0).get(0);
            switch (command) {
                case "LOG":
                    for (int i = 1; i < line.size(); i++) {
                        if (line.get(i).get(0).equals("PIPE")) {
                            System.out.println();
                            continue;
                        }
                        System.out.print(line.get(i).get(1) + " ");
                    }
                    break;

                case "MOVE":
                    if (line.size() < 3) break;
                    String dest = line.get(1).get(1);
                    if (!registers.containsKey(dest)) registers.put(dest, 0.0);
                    Double value = getValue(line.get(2));
                    if (value != null) registers.put(dest, value);
                    break;

                case "ADD":
                    if (line.size() < 4) break;
                    String addDest = line.get(1).get(1);
                    Double val1 = getValue(line.get(2));
                    Double val2 = getValue(line.get(3));
                    if (val1 != null && val2 != null) {
                        if (!registers.containsKey(addDest)) registers.put(addDest, 0.0);
                        registers.put(addDest, val1 + val2);
                    }
                    break;

                case "SUB":
                    if (line.size() < 4) break;
                    String subDest = line.get(1).get(1);
                    Double sub1 = getValue(line.get(2));
                    Double sub2 = getValue(line.get(3));
                    if (sub1 != null && sub2 != null) {
                        if (!registers.containsKey(subDest)) registers.put(subDest, 0.0);
                        registers.put(subDest, sub1 - sub2);
                    }
                    break;

                case "MUL":
                    if (line.size() < 4) break;
                    String mulDest = line.get(1).get(1);
                    Double mul1 = getValue(line.get(2));
                    Double mul2 = getValue(line.get(3));
                    if (mul1 != null && mul2 != null) {
                        if (!registers.containsKey(mulDest)) registers.put(mulDest, 0.0);
                        registers.put(mulDest, mul1 * mul2);
                    }
                    break;

                case "DIV":
                    if (line.size() < 4) break;
                    String divDest = line.get(1).get(1);
                    Double div1 = getValue(line.get(2));
                    Double div2 = getValue(line.get(3));
                    if (div1 != null && div2 != null && div2 != 0.0) {
                        if (!registers.containsKey(divDest)) registers.put(divDest, 0.0);
                        registers.put(divDest, div1 / div2);
                    }
                    break;

                case "TURN":
                    if (line.size() < 3) break;
                    String turnReg = line.get(1).get(1);
                    String direction = line.get(2).get(1);
                    if (registers.containsKey(turnReg)) {
                        Double currentVal = registers.get(turnReg);
                        if (direction.equals("right")) {
                            registers.put(turnReg, currentVal + 90);
                        } else if (direction.equals("left")) {
                            registers.put(turnReg, currentVal - 90);
                        }
                    }
                    break;

                case "WAIT":
                    if (line.size() < 2) break;
                    try {
                        Long waitTime = Long.parseLong(line.get(1).get(1));
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                    }
                    break;
            }
        }
    }

    public void printState() {
        System.out.println("\n=== VM State ===");
        System.out.println("Registers: " + registers);
        System.out.println("Memory: " + memory);
        System.out.println("Variables: " + variables);
    }
}

public class program {
    public static void main(String[] args) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("input.txt"));
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
        }
        Lexer lexer = new Lexer();
        ArrayList<ArrayList<ArrayList<String>>> tokens = lexer.tokenize(lines);
        execution exec = new execution();
        exec.createVM(8, 16);
        exec.execute(tokens);
        exec.printState();
    }
}
