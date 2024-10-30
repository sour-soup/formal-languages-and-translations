package laba1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String input;
        try {
            input = String.join("", Files.readAllLines(Paths.get("input.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Lexeme> lexemes = LexemeAnalyzer.analyze(input);
        LexemeTablePrinter.printLexemeTable(lexemes);
    }
}
