package com.mycompany.compilatorspyder;

public class ParseException extends Exception {
    public ParseException(TokenType expectedType, TokenType currentType, int linea) {
        super("Se esperaba el tipo " + expectedType + " pero se encontró " + currentType + " en el index " + linea);
    }

    public ParseException(String message) {
        super(message);
    }
}

