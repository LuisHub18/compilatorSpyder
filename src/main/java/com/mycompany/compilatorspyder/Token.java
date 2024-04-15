package com.mycompany.compilatorspyder;

public class Token {
        private String lexeme;
        private TokenType type;

        public Token(String lexeme, TokenType type) {
            this.lexeme = lexeme;
            this.type = type;
        }

        public String getLexeme() {
            return lexeme;
        }

        public TokenType getType() {
            return type;
        }

        @Override
        public String toString() {
            return "[" + lexeme + ", " + type + "]";
        }

}
