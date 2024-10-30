package laba1;

public record Lexeme(LexemeType type, LexemeCategory category, String value) {
    public enum LexemeType {
        DO("do"),
        WHILE("while"),
        LOOP("loop"),
        LOGICAL("logical"),
        OUTPUT("output"),
        IDENTIFIER("identifier"),
        CONSTANT("constant"),
        COMPARISON("comparison"),
        ARITHMETIC("arithmetic"),
        ASSIGNMENT("assignment"),
        SEMICOLON("semicolon");

        private final String value;

        LexemeType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum LexemeCategory {
        KEYWORD("keyword"),
        IDENTIFIER("identifier"),
        CONSTANT("constant"),
        OPERATION("operation"),
        SPECIAL_SYMBOL("special symbol"),
        UNDEFINED("undefined");

        private final String value;

        LexemeCategory(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
