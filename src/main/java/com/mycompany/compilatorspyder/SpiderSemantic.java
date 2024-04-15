package com.mycompany.compilatorspyder;

import java.util.List;

public class SpiderSemantic {
    private List<Token> tokens;
    private SymbolTable symbolTable;

    public SpiderSemantic(List<Token> tokens) {
        this.tokens = tokens;
        this.symbolTable = new SymbolTable();
    }

    public void analyze() throws SemanticException {
       buildSymbolTable();
       checkUnassignedValues();
       checkSumOfDifferentTypes();
    }
    
    public SymbolTable getSymbolTable(){
        return this.symbolTable;
    }

    private void buildSymbolTable() throws SemanticException {
        for (int i = 3; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.IDENTIFIER) {
                if (tokens.get(i + 1).getType() == TokenType.ASSIGNMENT) {
                    int value = 0;
                    int j=i+2;
                    int k = 0;
                    Token valueToken;
                    while ( tokens.get(j).getType() != TokenType.END_OF_LINE) {
                        valueToken = tokens.get(j);

                        System.out.println("valueToken: " + valueToken.getLexeme() + " " + valueToken.getType());
                        if (valueToken.getType() == TokenType.STRING) {
                            symbolTable.addSymbol(token.getLexeme(), new SymbolEntry(TokenType.STRING, valueToken.getLexeme()));
                        }
                        if (valueToken.getType() == TokenType.NUMBER || valueToken.getType() == TokenType.IDENTIFIER) {
                            int operator = 1;
                            if (tokens.get(i).getType() == TokenType.PLUS) {
                                operator = 1;
                            } else if (tokens.get(j).getType() == TokenType.MINUS) {
                                operator = -1;
                            }

                            SymbolEntry entry = symbolTable.getSymbolEntry(valueToken.getLexeme());
                            try{
                                int tokenValue = valueToken.getType() == TokenType.IDENTIFIER ?
                                        Integer.parseInt(entry.getValue()) : Integer.parseInt(valueToken.getLexeme());
                                value += operator * tokenValue;
                            }catch(Exception e){
                                //throw new SemanticException("El identificador '" + valueToken.getLexeme() + "' no tiene un valor asignado o tiene un valor invalido.");
                            }
                        }
                        j++;
                        k++;
                    }

                    if(symbolTable.containsSymbol(token.getLexeme()))
                        continue;
                    //Se calcula el valor si es una expresion pero lo dejamos igual a cero para efectos practicos
                    if(k>1)
                        symbolTable.addSymbol(token.getLexeme(), new SymbolEntry(TokenType.NUMBER, String.valueOf(0)));
                    else if(k==1)
                        symbolTable.addSymbol(token.getLexeme(), new SymbolEntry(TokenType.NUMBER, String.valueOf(value)));
                }
            }
        }
    }

    private void checkUnassignedValues() throws SemanticException {
        System.out.println("Checking unassigned values");
        for (int i = 3; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.IDENTIFIER) {
                SymbolEntry symbolEntry = symbolTable.getSymbolEntry(token.getLexeme());
                if (symbolEntry == null || symbolEntry.getValue() == null) {
                    throw new SemanticException("Identificador sin valor asignado: " + token.getLexeme());
                }
            }
        }
    }

    private void checkSumOfDifferentTypes() throws SemanticException {
        System.out.println("Checking sum of different types");
        for (int i = 0; i < tokens.size() - 2; i++) {
            Token token1 = tokens.get(i);
            Token token2 = tokens.get(i + 1);
            Token token3 = tokens.get(i + 2);

            // Caso: STRING + NUMBER
            if (token1.getType() == TokenType.STRING && token2.getType() == TokenType.PLUS &&
                    token3.getType() == TokenType.NUMBER) {
                throw new SemanticException("No se puede sumar un STRING y un NUMBER en la posición " + i);
            }

            // Caso: NUMBER + STRING
            if (token1.getType() == TokenType.NUMBER && token2.getType() == TokenType.PLUS &&
                    token3.getType() == TokenType.STRING) {
                throw new SemanticException("No se puede sumar un NUMBER y un STRING en la posición " + i);
            }

            // Caso: IDENTIFIER(NUMBER) + NUMBER
            if (token1.getType() == TokenType.IDENTIFIER && token2.getType() == TokenType.PLUS &&
                    token3.getType() == TokenType.NUMBER) {
                SymbolEntry entry1 = symbolTable.getSymbolEntry(token1.getLexeme());
                if (entry1 != null && entry1.getType() == TokenType.NUMBER) {

                    continue;
                }else  if (entry1 != null && entry1.getType() == TokenType.STRING) {
                    //Case: IDENTIFIER(String) + NUMBER
                    throw new SemanticException("No se puede sumar un IDENTIFIER con valor " + entry1.getValue() + " y un NUMBER en la posición " + i);
                } else {
                    try {
                        Integer.parseInt(token3.getLexeme());
                        continue;
                    } catch (NumberFormatException e) {
                        throw new SemanticException("No se puede sumar un IDENTIFIER con valor " + token3.getLexeme() + " y un NUMBER en la posición " + i);
                    }
                }
            }

            // Caso: NUMBER + IDENTIFIER(NUMBER)
            if (token1.getType() == TokenType.NUMBER && token2.getType() == TokenType.PLUS &&
                    token3.getType() == TokenType.IDENTIFIER) {
                SymbolEntry entry3 = symbolTable.getSymbolEntry(token3.getLexeme());
                if ( !(entry3 != null && entry3.getType() == TokenType.NUMBER) ) {
                    assert entry3 != null;
                    throw new SemanticException("No se puede sumar un NUMBER y un IDENTIFIER con valor " + entry3.getValue() + " en la posición " + i);
                }
            }
        }
    }
}
