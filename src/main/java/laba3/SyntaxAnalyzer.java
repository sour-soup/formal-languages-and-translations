package laba3;

import laba1.Lexeme;
import laba1.Lexeme.LexemeType;
import laba2.SyntaxException;

import java.util.Iterator;
import java.util.List;

public class SyntaxAnalyzer {
    private final Iterator<Lexeme> iterator;
    private Lexeme currentLexeme;
    private final PolizGenerator polizGenerator;

    public SyntaxAnalyzer(List<Lexeme> lexemes, PolizGenerator polizGenerator) {
        this.iterator = lexemes.iterator();
        this.currentLexeme = iterator.hasNext() ? iterator.next() : null;
        this.polizGenerator = polizGenerator;
    }

    public void analyze() {
        parseDoWhileStatement();
        if (currentLexeme != null) {
            throw new SyntaxException("В конце остались необработанные лексемы: " + currentLexeme);
        }
    }

    private void parseDoWhileStatement() {
        int startLoop = polizGenerator.getPolizEntries().size();

        expect(LexemeType.DO);
        expect(LexemeType.WHILE);
        parseLogicalExpression();

        int jumpPosition = polizGenerator.addJumpAddress(-1);
        polizGenerator.addCommand(Command.JZ);

        parseOperators();

        polizGenerator.addJumpAddress(startLoop);
        polizGenerator.addCommand(Command.JMP);

        expect(LexemeType.LOOP);

        polizGenerator.setJumpAddress(jumpPosition, polizGenerator.getPolizEntries().size());
    }

    private void parseLogicalExpression() {
        parseLogicalSubExpression();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("or")) {
            expect(LexemeType.LOGICAL);
            parseLogicalSubExpression();
            polizGenerator.addCommand(Command.OR);
        }
    }

    private void parseLogicalSubExpression() {
        if (currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("not")) {
            expect(LexemeType.LOGICAL);
            parseComparisonExpression();
            polizGenerator.addCommand(Command.NOT);
        } else {
            parseComparisonExpression();
        }

        while (currentLexeme != null && currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("and")) {
            expect(LexemeType.LOGICAL);
            if (currentLexeme.type() == LexemeType.LOGICAL && currentLexeme.value().equals("not")) {
                expect(LexemeType.LOGICAL);
                parseComparisonExpression();
                polizGenerator.addCommand(Command.NOT);
            } else {
                parseComparisonExpression();
            }
            polizGenerator.addCommand(Command.AND);
        }
    }

    private void parseComparisonExpression() {
        parseOperand();
        if (currentLexeme != null && currentLexeme.type() == LexemeType.COMPARISON) {
            String operator = currentLexeme.value();
            expect(LexemeType.COMPARISON);
            parseOperand();

            Command command = switch (operator) {
                case "<" -> Command.CMPL;
                case ">" -> Command.CMPG;
                case "==" -> Command.CMPE;
                case "<>" -> Command.CMPNE;
                case "<=" -> Command.CMPLE;
                case ">=" -> Command.CMPGE;
                default -> throw new SyntaxException("Неизвестный оператор сравнения: " + operator);
            };
            polizGenerator.addCommand(command);
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
            String variable = currentLexeme.value();
            expect(LexemeType.IDENTIFIER);
            expect(LexemeType.ASSIGNMENT);
            parseArithmeticExpression();

            polizGenerator.addCommand(Command.SET);
            polizGenerator.addVariable(variable);
        } else if (currentLexeme.type() == LexemeType.OUTPUT) {
            expect(LexemeType.OUTPUT);
            parseOperand();
            polizGenerator.addCommand(Command.OUTPUT);
        }
    }

    private void parseArithmeticExpression() {
        parseArithmeticSubExpression();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.ARITHMETIC
               && "+-".contains(currentLexeme.value())) {
            Command command = currentLexeme.value().equals("+") ? Command.ADD : Command.SUB;
            expect(LexemeType.ARITHMETIC);
            parseArithmeticSubExpression();
            polizGenerator.addCommand(command);
        }
    }

    private void parseArithmeticSubExpression() {
        parseOperand();
        while (currentLexeme != null && currentLexeme.type() == LexemeType.ARITHMETIC
               && "*/".contains(currentLexeme.value())) {
            Command command = currentLexeme.value().equals("*") ? Command.MUL : Command.DIV;
            expect(LexemeType.ARITHMETIC);
            parseOperand();
            polizGenerator.addCommand(command);
        }
    }

    private void parseOperand() {
        if (currentLexeme != null && currentLexeme.type() == LexemeType.IDENTIFIER) {
            polizGenerator.addVariable(currentLexeme.value());
            expect(LexemeType.IDENTIFIER);
        } else if (currentLexeme != null && currentLexeme.type() == LexemeType.CONSTANT) {
            int constantValue = Integer.parseInt(currentLexeme.value());
            polizGenerator.addConstant(constantValue);
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
