package laba1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StringBuilder inputBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                inputBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        String input = inputBuilder.toString();
        List<Lexeme> lexemes = LexemeAnalyzer.analyze(input);
        LexemeTablePrinter.printLexemeTable(lexemes);
    }
}
