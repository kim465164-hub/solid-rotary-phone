import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class sus {
    public static void main(String[] args) {
        boolean debug = true;
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter file path: ");
        String filePath = sc.nextLine();
        
        try {
            // Read file content
            String code = new String(Files.readAllBytes(Paths.get(filePath)));
            
            // Create lexer and tokenize
            lexer lex = new lexer(code);
            
            // Display tokens in grid format
            if (debug == true) {
                System.out.println("\n--- Tokens in Grid Format ---");
                lex.displayTokens();
            }
            // Get and print tokens using getTokens() method
            ArrayList<ArrayList<ArrayList<String>>> tokens = lex.getTokens();
            if (debug == true) {
                System.out.println("\n--- Tokens from getTokens() ---");
                for (int i = 0; i < tokens.size(); i++) {
                    System.out.println("Line " + (i + 1) + ": " + tokens.get(i));
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
        sc.close();
    }
}