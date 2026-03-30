import java.util.ArrayList;
import java.util.HashMap;

public class lexer {
    enum tokenType {
        KEYWORD, IDENTIFIER, LITERAL, OPERATOR, SEPARATOR, COMMENT
    }
    
    static HashMap<String, String> KEYWORDS = new HashMap<>();
    static HashMap<String, String> OPERATORS = new HashMap<>();
    static HashMap<String, String> SEPARATORS = new HashMap<>();
    
    static {
        // Keywords
        String[] keywords = {"if", "else", "while", "for", "return", "class", "public", "private", 
            "protected", "static", "void", "int", "float", "string", "print", "input", "new", 
            "this", "super", "extends", "let", "const", "var", "function", "try", "catch", 
            "finally", "throw", "import", "export", "module"};
        for (String kw : keywords) {
            KEYWORDS.put(kw, kw.toUpperCase());
        }
        
        // Operators
        OPERATORS.put("+", "PLUS");
        OPERATORS.put("-", "MINUS");
        OPERATORS.put("*", "MULTIPLY");
        OPERATORS.put("/", "DIVIDE");
        OPERATORS.put("=", "ASSIGN");
        OPERATORS.put("==", "EQUALS");
        OPERATORS.put("!=", "NOT_EQUALS");
        OPERATORS.put("<", "LESS_THAN");
        OPERATORS.put(">", "GREATER_THAN");
        OPERATORS.put("<=", "LESS_EQUAL");
        OPERATORS.put(">=", "GREATER_EQUAL");
        
        // Separators
        SEPARATORS.put("(", "LEFT_PAREN");
        SEPARATORS.put(")", "RIGHT_PAREN");
        SEPARATORS.put("{", "LEFT_BRACE");
        SEPARATORS.put("}", "RIGHT_BRACE");
        SEPARATORS.put("[", "LEFT_BRACKET");
        SEPARATORS.put("]", "RIGHT_BRACKET");
        SEPARATORS.put(";", "SEMICOLON");
        SEPARATORS.put(",", "COMMA");
        SEPARATORS.put(".", "DOT");
    }

    private ArrayList<ArrayList<ArrayList<String>>> lineTokens = new ArrayList<>();
    
    private void addToken(int line, int column, String type, String value) {
        while (lineTokens.size() <= line) {
            lineTokens.add(new ArrayList<>());
        }
        ArrayList<String> token = new ArrayList<>();
        token.add(type);
        token.add(value);
        token.add(String.valueOf(line));
        token.add(String.valueOf(column));
        lineTokens.get(line).add(token);
    }
    
    public lexer(String code) {
        String[] lines = code.split("\n");
        int lineNum = 0;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                lineNum++;
                continue;
            }
            
            int i = 0;
            int columnCount = 0;
            while (i < line.length()) {
                // Skip whitespace
                while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                if (i >= line.length()) break;
                
                int tokenStartCol = columnCount;
                
                // Check for string literals
                if (line.charAt(i) == '"' || line.charAt(i) == '\'') {
                    char quote = line.charAt(i);
                    String token = "";
                    token += quote;
                    i++;
                    while (i < line.length() && line.charAt(i) != quote) {
                        token += line.charAt(i);
                        i++;
                    }
                    if (i < line.length()) token += line.charAt(i++);
                    addToken(lineNum, columnCount, "LITERAL", token);
                    columnCount++;
                } 
                // Check for two-character operators
                else if (i + 1 < line.length() && OPERATORS.containsKey(line.substring(i, i + 2))) {
                    String op = line.substring(i, i + 2);
                    addToken(lineNum, columnCount, "OPERATOR", op);
                    i += 2;
                    columnCount++;
                }
                // Check for single-character operators
                else if (OPERATORS.containsKey(String.valueOf(line.charAt(i)))) {
                    addToken(lineNum, columnCount, "OPERATOR", String.valueOf(line.charAt(i)));
                    i++;
                    columnCount++;
                }
                // Check for separators
                else if (SEPARATORS.containsKey(String.valueOf(line.charAt(i)))) {
                    addToken(lineNum, columnCount, "SEPARATOR", String.valueOf(line.charAt(i)));
                    i++;
                    columnCount++;
                }
                // Check for numbers
                else if (Character.isDigit(line.charAt(i))) {
                    String token = "";
                    while (i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.')) {
                        token += line.charAt(i);
                        i++;
                    }
                    addToken(lineNum, columnCount, "LITERAL", token);
                    columnCount++;
                }
                // Check for identifiers and keywords
                else if (Character.isLetter(line.charAt(i)) || line.charAt(i) == '_') {
                    String token = "";
                    while (i < line.length() && (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '_')) {
                        token += line.charAt(i);
                        i++;
                    }
                    
                    if (KEYWORDS.containsKey(token.toLowerCase())) {
                        addToken(lineNum, columnCount, "KEYWORD", token);
                    } else {
                        addToken(lineNum, columnCount, "IDENTIFIER", token);
                    }
                    columnCount++;
                } else {
                    i++;
                }
            }
            
            lineNum++;
        }
    }
    
    public ArrayList<ArrayList<ArrayList<String>>> getTokens() {
        return lineTokens;
    }
    
    
    public void displayTokens() {
        System.out.println("[");
        int maxLines = Math.min(lineTokens.size(), 100);
        int lineWidth = String.valueOf(maxLines).length() + 1;
        
        for (int i = 0; i < lineTokens.size(); i++) {
            String lineNum = String.format("%" + lineWidth + "d", i + 1);
            System.out.print("    " + lineNum + " [");
            int tokenCount = lineTokens.get(i).size();
            int columns = 4;
            
            // Display up to 4 tokens per row with line and column info
            for (int col = 0; col < columns; col++) {
                System.out.print("[");
                if (col < tokenCount) {
                    ArrayList<String> token = lineTokens.get(i).get(col);
                    String type = token.get(0);
                    String value = token.get(1);
                    String line = token.get(2);
                    String column = token.get(3);
                    System.out.print(type + ": " + value + " (L:" + line + " C:" + column + ")");
                }
                System.out.print("]");
                if (col < columns - 1) System.out.print(", ");
            }
            System.out.println("], line " + (i + 1));
        }
        
        // Print column headers
        System.out.println("     1 2 3 4");
        System.out.println("] one program");
    }
}