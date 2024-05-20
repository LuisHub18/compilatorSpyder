package com.mycompany.compilatorspyder;

import java.util.Arrays;
import java.util.List;

public class SpiderParser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public SpiderParser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    public void parse() throws ParseException {
        parseBlock();
        if (currentTokenIndex < tokens.size()) {
            throw new ParseException("Tokens adicionales al final del análisis.");
        }
    }

    private void parseBlock() throws ParseException {
        match(TokenType.BLOCK);
        match(TokenType.IDENTIFIER); // Verifica el nombre del bloque
        match(TokenType.BLOCK_OPEN); // Verifica la apertura del bloque

         // Verifica si todavía hay tokens disponibles y si no se ha encontrado el cierre del bloque
        while (currentTokenIndex < tokens.size() && getCurrentToken().getType() != TokenType.BLOCK_CLOSE) {
            if (getCurrentToken().getType() == TokenType.PRINT) {
                parsePrint(); // Analiza la instrucción print
            } else {
                parseAssignment(); // Analiza cada asignación dentro del bloque
            }
        }
        match(TokenType.BLOCK_CLOSE); // Verifica el cierre del bloque
    }

    private void parsePrint() throws ParseException {
        match(TokenType.PRINT); // Verifica la instrucción print
        match(TokenType.OPEN_PARENTHESIS);
        match(TokenType.STRING, TokenType.IDENTIFIER, TokenType.NUMBER); // Verifica el argumento de print
        // Permite la concatenación de múltiples argumentos
        while (getCurrentToken().getType() == TokenType.PLUS) {
            match(TokenType.PLUS);
            match(TokenType.STRING, TokenType.IDENTIFIER, TokenType.NUMBER);
        }
        match(TokenType.CLOSE_PARENTHESIS);
        match(TokenType.END_OF_LINE); // Verifica el final de la instrucción print
    }

    private void parseAssignment() throws ParseException {
        match(TokenType.IDENTIFIER); // Verifica el identificador
        match(TokenType.ASSIGNMENT); // Verifica el operador de asignación
        parseExpression(); // Analiza la expresión
    }

    private void parseExpression() throws ParseException {
        // Una expresión debe comenzar con un identificador o un número
        match(TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.STRING);
        // Después del primer token en la expresión, puede haber operadores como '+' o '-'
        while (getCurrentToken().getType() == TokenType.PLUS ||
                getCurrentToken().getType() == TokenType.MINUS) {
            match(TokenType.PLUS, TokenType.MINUS); // Verifica el operador '+'
            match(TokenType.IDENTIFIER, TokenType.NUMBER, TokenType.STRING); // Verifica el siguiente identificador o número
        }
        match(TokenType.END_OF_LINE); // Verifica el final de la expresión
    }

    private void match(TokenType... expectedTypes) throws ParseException {
        boolean anyMatch = Arrays.stream(expectedTypes).anyMatch(x -> x == getCurrentToken().getType());

        if (!anyMatch)
            throw new ParseException(expectedTypes[0], getCurrentToken().getType(), currentTokenIndex);
        else
            currentTokenIndex++;
    }

    private Token getCurrentToken() {
        if (currentTokenIndex + 1 > tokens.size()){
            return tokens.get(tokens.size()-1);
        }
        return tokens.get(currentTokenIndex);
    }
}
