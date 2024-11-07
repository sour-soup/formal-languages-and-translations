package laba2;

import laba1.Lexeme;
import laba1.LexemeAnalyzer;
import laba1.LexemeTablePrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String input;
        try {
            input = String.join("\n", Files.readAllLines(Paths.get("input.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Lexeme> lexemes = LexemeAnalyzer.analyze(input);
        LexemeTablePrinter.printLexemeTable(lexemes);

        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexemes);
        try {
            syntaxAnalyzer.analyze();
            System.out.println("Синтаксический анализ выполнен успешно!");
        }
        catch (SyntaxException e){
            System.out.println("Синтаксическая ошибка: " + e.getMessage());
        }
    }
}
