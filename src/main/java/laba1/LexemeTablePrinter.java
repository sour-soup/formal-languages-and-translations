package laba1;

import com.jakewharton.fliptables.FlipTable;

import java.util.List;

public class LexemeTablePrinter {
    public static void printLexemeTable(List<Lexeme> lexemes) {
        String[] headers = {"Значение", "Тип лексемы", "Категория лексемы"};

        String[][] data = new String[lexemes.size()][3];

        for (int i = 0; i < lexemes.size(); i++) {
            Lexeme lexeme = lexemes.get(i);
            data[i][0] = lexeme.value();
            data[i][1] = lexeme.type().toString();
            data[i][2] = lexeme.category().toString();
        }

        System.out.println(FlipTable.of(headers, data));
    }
}
