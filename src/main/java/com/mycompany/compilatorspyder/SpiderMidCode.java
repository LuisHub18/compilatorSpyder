/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilatorspyder;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author EduTQ
 */
public class SpiderMidCode {
    SymbolTable symbolTable;
    List<Token> tokens;

    public SpiderMidCode(SymbolTable symbolTable, List<Token> tokens) {
        this.symbolTable = symbolTable;
        this.tokens = tokens;
    }

    public String bitToHex(int bits){
        BigInteger bigInteger = BigInteger.valueOf(bits);
        return String.format("%04X", bigInteger);
    }
    public String generateAssemblyIntelCode(){
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t.DATA\n");
        int producciones = 0;

        for (String identifier : symbolTable.getSymbolMap().keySet()) {
           if (symbolTable.getType(identifier) == TokenType.NUMBER && !symbolTable.getSymbolEntry(identifier).getValue().equals("0")) {
               producciones+=4;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DD \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           }else if (symbolTable.getType(identifier) == TokenType.STRING && !symbolTable.getSymbolEntry(identifier).getValue().equals("0")) {
               producciones+=8;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DB \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           } else if (symbolTable.getType(identifier) == TokenType.NUMBER) {
               producciones+=4;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DD \t ?\n");
           } else if (symbolTable.getType(identifier) == TokenType.STRING) {
               producciones+=8;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DB \t ?\n");
           }
        }
        producciones = 0;
        sb.append("\n");
        sb.append("\t\t.CODE\n");
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == TokenType.PLUS) {
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \tAX, ").append(tokens.get(i-1).getLexeme()).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tADD \tAX, ").append(tokens.get(i+1).getLexeme()).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \t").append(tokens.get(i-3).getLexeme()).append(", AX\n");
            } else if(tokens.get(i).getType() == TokenType.MINUS){
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \tAX, ").append(tokens.get(i-1).getLexeme()).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tSUB \tAX, ").append(tokens.get(i+1).getLexeme()).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \t").append(tokens.get(i-3).getLexeme()).append(", AX\n");
            }
        }
        return sb.toString();
    }
}
