package laba4;

import laba1.Lexeme;
import laba1.LexemeAnalyzer;
import laba1.LexemeTablePrinter;
import laba2.SyntaxException;
import laba3.PolizEntry;
import laba3.PolizGenerator;
import laba3.SyntaxAnalyzer;

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
        PolizGenerator polizGenerator = new PolizGenerator();

        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexemes, polizGenerator);
        try {
            syntaxAnalyzer.analyze();
            System.out.println("Синтаксический анализ выполнен успешно!");
        } catch (SyntaxException e) {
            System.out.println("Синтаксическая ошибка: " + e.getMessage());
            return;
        }
        System.out.println();

        List<PolizEntry<Object>> polizEntries = polizGenerator.getPolizEntries();
        for (int i = 0; i < polizEntries.size(); i++) {
            System.out.printf("%d -> (%s %s)%n",
                    i,
                    polizEntries.get(i).type().toString(),
                    polizEntries.get(i).value().toString());
        }
        System.out.println("\nИнтерпретация:");
        Interpreter interpreter = new Interpreter(polizEntries);
        interpreter.execute();
    }
}
