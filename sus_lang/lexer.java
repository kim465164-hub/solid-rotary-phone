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
    
    private void addToken(int line, String type, String value) {
        while (lineTokens.size() <= line) {
            lineTokens.add(new ArrayList<>());
        }
        ArrayList<String> token = new ArrayList<>();
        token.add(type);
        token.add(value);
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
            while (i < line.length()) {
                // Skip whitespace
                while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                if (i >= line.length()) break;
                
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
                    addToken(lineNum, "LITERAL", token);
                } 
                // Check for two-character operators
                else if (i + 1 < line.length() && OPERATORS.containsKey(line.substring(i, i + 2))) {
                    String op = line.substring(i, i + 2);
                    addToken(lineNum, "OPERATOR", op);
                    i += 2;
                }
                // Check for single-character operators
                else if (OPERATORS.containsKey(String.valueOf(line.charAt(i)))) {
                    addToken(lineNum, "OPERATOR", String.valueOf(line.charAt(i)));
                    i++;
                }
                // Check for separators
                else if (SEPARATORS.containsKey(String.valueOf(line.charAt(i)))) {
                    addToken(lineNum, "SEPARATOR", String.valueOf(line.charAt(i)));
                    i++;
                }
                // Check for numbers
                else if (Character.isDigit(line.charAt(i))) {
                    String token = "";
                    while (i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.')) {
                        token += line.charAt(i);
                        i++;
                    }
                    addToken(lineNum, "LITERAL", token);
                }
                // Check for identifiers and keywords
                else if (Character.isLetter(line.charAt(i)) || line.charAt(i) == '_') {
                    String token = "";
                    while (i < line.length() && (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '_')) {
                        token += line.charAt(i);
                        i++;
                    }
                    
                    if (KEYWORDS.containsKey(token.toLowerCase())) {
                        addToken(lineNum, "KEYWORD", token);
                    } else {
                        addToken(lineNum, "IDENTIFIER", token);
                    }
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
        for (int i = 0; i < lineTokens.size(); i++) {
            System.out.print("    [");
            int tokenCount = lineTokens.get(i).size();
            int columns = 4;
            
            // Display 4 columns per row
            for (int col = 0; col < columns; col++) {
                System.out.print("[");
                if (col < tokenCount) {
                    ArrayList<String> token = lineTokens.get(i).get(col);
                    System.out.print(token.get(0) + ": " + token.get(1));
                }
                System.out.print("]");
                if (col < columns - 1) System.out.print(", ");
            }
            System.out.println("], line " + (i + 1));
        }
        System.out.println("     1 2 3 4");
        System.out.println("] one program");
    }
}