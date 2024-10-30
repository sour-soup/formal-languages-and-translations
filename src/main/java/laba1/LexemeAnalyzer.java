package laba1;

import laba1.Lexeme.LexemeCategory;
import laba1.Lexeme.LexemeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexemeAnalyzer {
    private static final List<Character> SPECIAL_SYMBOLS = List.of('<', '>', '+', '-', '*', '/', '=', ';');

    private static final Map<String, LexemeType> KEYWORDS_TYPE = Map.of("not", LexemeType.LOGICAL, "and", LexemeType.LOGICAL, "or", LexemeType.LOGICAL, "do", LexemeType.DO, "while", LexemeType.WHILE, "loop", LexemeType.LOOP, "output", LexemeType.OUTPUT);

    private LexemeAnalyzer() {
    }

    public static List<Lexeme> analyze(String input) {
        List<Lexeme> lexemes = new ArrayList<>();

        input = input + '\n';
        State currentState = State.START;
        StringBuilder currentLexeme = new StringBuilder();

        int index = 0;
        while (index < input.length()) {
            char currentSymbol = input.charAt(index);
            int add = 1;
            final boolean isLetterOrDigitOrWhileSpace = Character.isLetterOrDigit(currentSymbol) || Character.isWhitespace(currentSymbol);
            final boolean isSpecialSymbolOrWhiteSpace = SPECIAL_SYMBOLS.contains(currentSymbol) || Character.isWhitespace(currentSymbol);

            currentState = switch (currentState) {
                case START -> {
                    if (Character.isWhitespace(currentSymbol)) {
                        yield State.START;
                    } else if (Character.isDigit(currentSymbol)) {
                        currentLexeme.append(currentSymbol);
                        yield State.CONSTANT;
                    } else if (Character.isLetter(currentSymbol)) {
                        currentLexeme.append(currentSymbol);
                        yield State.IDENTIFIER;
                    } else if (currentSymbol == '<') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_L;
                    } else if (currentSymbol == '>') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_R;
                    } else if (currentSymbol == '+' || currentSymbol == '-' || currentSymbol == '*' || currentSymbol == '/') {
                        currentLexeme.append(currentSymbol);
                        yield State.ARITHMETIC;
                    } else if (currentSymbol == '=') {
                        currentLexeme.append(currentSymbol);
                        yield State.ASSIGNMENT;
                    } else if (currentSymbol == ';') {
                        lexemes.add(new Lexeme(LexemeType.SEMICOLON, LexemeCategory.SPECIAL_SYMBOL, ";"));
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case IDENTIFIER -> {
                    if (Character.isLetterOrDigit(currentSymbol)) {
                        currentLexeme.append(currentSymbol);
                        yield State.IDENTIFIER;
                    } else if (isSpecialSymbolOrWhiteSpace) {
                        LexemeType lexemeType = KEYWORDS_TYPE.getOrDefault(currentLexeme.toString(), LexemeType.IDENTIFIER);
                        LexemeCategory lexemeCategory = KEYWORDS_TYPE.containsKey(currentLexeme.toString()) ?
                                LexemeCategory.KEYWORD : LexemeCategory.IDENTIFIER;

                        lexemes.add(new Lexeme(lexemeType, lexemeCategory, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case CONSTANT -> {
                    if (Character.isDigit(currentSymbol)) {
                        currentLexeme.append(currentSymbol);
                        yield State.CONSTANT;
                    } else if (isSpecialSymbolOrWhiteSpace) {
                        lexemes.add(new Lexeme(LexemeType.CONSTANT, LexemeCategory.CONSTANT, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case ARITHMETIC -> {
                    if (isLetterOrDigitOrWhileSpace) {
                        lexemes.add(new Lexeme(LexemeType.ARITHMETIC, LexemeCategory.OPERATION, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case COMPARISON_L -> {
                    if (currentSymbol == '=') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_E;
                    } else if (currentSymbol == '>') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_NE;
                    } else if (isLetterOrDigitOrWhileSpace) {
                        lexemes.add(new Lexeme(LexemeType.COMPARISON, LexemeCategory.OPERATION, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case COMPARISON_R -> {
                    if (currentSymbol == '=') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_E;
                    } else if (isLetterOrDigitOrWhileSpace) {
                        lexemes.add(new Lexeme(LexemeType.COMPARISON, LexemeCategory.OPERATION, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case ASSIGNMENT -> {
                    if (currentSymbol == '=') {
                        currentLexeme.append(currentSymbol);
                        yield State.COMPARISON_E;
                    } else if (isLetterOrDigitOrWhileSpace) {
                        lexemes.add(new Lexeme(LexemeType.ASSIGNMENT, LexemeCategory.OPERATION, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case COMPARISON_E, COMPARISON_NE -> {
                    if (isLetterOrDigitOrWhileSpace) {
                        lexemes.add(new Lexeme(LexemeType.COMPARISON, LexemeCategory.OPERATION, currentLexeme.toString()));
                        currentLexeme.setLength(0);
                        add = 0;
                        yield State.START;
                    } else {
                        add = 0;
                        yield State.ERROR;
                    }
                }
                case ERROR -> {
                    System.err.println("Ошибка: недопустимый символ '" + currentSymbol + "' на позиции " + index);
                    currentLexeme.setLength(0);
                    yield State.START;
                }
            };
            index += add;
        }
        return lexemes;
    }

    private enum State {
        START, IDENTIFIER, CONSTANT, COMPARISON_L, COMPARISON_R, COMPARISON_NE, COMPARISON_E, ARITHMETIC, ASSIGNMENT, ERROR
    }
}