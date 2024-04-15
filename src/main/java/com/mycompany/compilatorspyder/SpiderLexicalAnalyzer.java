package com.mycompany.compilatorspyder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpiderLexicalAnalyzer {

    public static List<Token> analyze(String sourceCode) throws LexicalException{
        List<Token> tokens = new ArrayList<>();

        String identifierPattern = "[a-zA-Z][a-zA-Z0-9_]*"; // Identificador: una letra seguida de cero o más letras, dígitos o guiones bajos
        String assignmentPattern = ":="; // Asignación
        String plusPattern = "\\+"; // Suma
        String minusPattern = "-"; // Resta
        String blockOpenPattern = "\\{"; // Llave de apertura de bloque
        String blockClosePattern = "}"; // Llave de cierre de bloque
        String numberPattern = "\\d+"; // Número: uno o más dígitos
        String stringPattern = "\"[^\"]*\""; // Cadena: cualquier texto encerrado entre comillas dobles
        String commentPattern = "#.*"; // Comentario: cualquier texto después de #
        String blockPattern = "block"; // Palabras reservadas
        String printPattern = "print"; // Palabras reservadas
        String unknownPattern = "[@%&]";
        String endOfLinePattern = ";"; // Fin de línea

        String regexPattern = identifierPattern +"|"+ assignmentPattern + "|" + plusPattern + "|" + minusPattern + "|" + blockOpenPattern + "|" + blockClosePattern + "|" + numberPattern + "|" + stringPattern + "|"+unknownPattern+"|" + commentPattern + "|" + endOfLinePattern;
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(sourceCode);

        // Lógica para encontrar tokens y agregarlos a la lista
        while (matcher.find()) {
            String token = matcher.group().trim();
            TokenType type;
            if(token.matches(blockPattern)) {
                type = TokenType.BLOCK;
            }else if (token.matches(printPattern)) {
                type = TokenType.PRINT;
            }else if (token.matches(numberPattern)) {
                type = TokenType.NUMBER;
            } else if (token.matches(stringPattern)) {
                type = TokenType.STRING;
            } else if (token.matches(commentPattern)) {
                type = TokenType.COMMENT;
            } else if (token.matches(identifierPattern)) {
                type = TokenType.IDENTIFIER;
            } else if (token.matches(blockOpenPattern)) {
                type = TokenType.BLOCK_OPEN;
            } else if (token.matches(assignmentPattern)) {
                type = TokenType.ASSIGNMENT;
            } else if (token.matches(plusPattern)) {
                type = TokenType.PLUS;
            } else if (token.matches(minusPattern)) {
                type = TokenType.MINUS;
            } else if (token.matches(blockClosePattern)) {
                type = TokenType.BLOCK_CLOSE;
            } else if (token.matches(endOfLinePattern)) {
                type = TokenType.END_OF_LINE;
             } else if (token.matches(unknownPattern)){
                type = TokenType.UNKNOWN;
            } else{
                throw new LexicalException("Token desconocido");
            }
            tokens.add(new Token(token, type));
        }
        return tokens;
    }
}