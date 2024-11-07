package laba2;

import laba1.Lexeme;
import laba1.Lexeme.LexemeType;

import java.util.Iterator;
import java.util.List;

public class SyntaxAnalyzer {
    private final Iterator<Lexeme> iterator;
    private Lexeme currentLexeme;

    public SyntaxAnalyzer(List<Lexeme> lexemes) {
        this.iterator = lexemes.iterator();
        this.currentLexeme = iterator.hasNext() ? iterator.next() : null;
    }

    public void analyze() {
        parseDoWhileStatement();
        if (currentLexeme != null) {
            throw new SyntaxException("В конце остались необработанные лексемы: " + currentLexeme);
        }
    }

    private void parseDoWhileStatement() {
        expect(LexemeType.DO);
        expect(LexemeType.WHILE);
        parseLogicalExpression();
        parseOperators();
        expect(LexemeType.LOOP);
    }

    private void parseLogicalExpression() {
        parseLogicalSubExpression();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("or")) {
            expect(LexemeType.LOGICAL);
            parseLogicalSubExpression();
        }
    }

    private void parseLogicalSubExpression() {
        if (currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("not")) {
            expect(LexemeType.LOGICAL);
        }
        parseComparisonExpression();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("and")) {
            expect(LexemeType.LOGICAL);
            if (currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("not")) {
                expect(LexemeType.LOGICAL);
            }
            parseComparisonExpression();
        }
    }

    private void parseComparisonExpression() {
        parseOperand();
        if (currentLexeme != null && (currentLexeme.type() == LexemeType.COMPARISON)) {
            expect(LexemeType.COMPARISON);
            parseOperand();
        }
    }

    private void parseOperators() {
        parseOperator();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.SEMICOLON) {
            expect(LexemeType.SEMICOLON);
            parseOperator();
        }
    }

    private void parseOperator() {
        if (currentLexeme.type() == LexemeType.IDENTIFIER) {
            expect(LexemeType.IDENTIFIER);
            expect(LexemeType.ASSIGNMENT);
            parseArithmeticExpression();
        } else if (currentLexeme.type() == LexemeType.OUTPUT) {
            expect(LexemeType.OUTPUT);
            parseOperand();
        }
    }

    private void parseArithmeticExpression() {
        parseArithmeticSubExpression();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.ARITHMETIC
               && "*/".contains(currentLexeme.value())) {
            expect(LexemeType.ARITHMETIC);
            parseArithmeticSubExpression();
        }
    }

    private void parseArithmeticSubExpression() {
        parseOperand();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.ARITHMETIC
               && "+-".contains(currentLexeme.value())) {
            expect(LexemeType.ARITHMETIC);
            parseOperand();
        }
    }

    private void parseOperand() {
        if (currentLexeme != null && currentLexeme.type() == LexemeType.IDENTIFIER) {
            expect(LexemeType.IDENTIFIER);
        } else if (currentLexeme != null && currentLexeme.type() == LexemeType.CONSTANT) {
            expect(LexemeType.CONSTANT);
        } else {
            String actualLexeme = currentLexeme != null ? currentLexeme.toString() : "конец ввода";
            throw new SyntaxException("Ожидался идентификатор или константа, но найден: " + actualLexeme);
        }
    }

    private void expect(LexemeType type) {
        if (currentLexeme == null || currentLexeme.type() != type) {
            String actualLexeme = currentLexeme != null ? currentLexeme.toString() : "конец ввода";
            throw new SyntaxException("Ожидался " + type + ", но найден: " + actualLexeme);
        }
        currentLexeme = iterator.hasNext() ? iterator.next() : null;
    }
}
