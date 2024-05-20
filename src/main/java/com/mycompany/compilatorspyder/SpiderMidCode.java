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
               producciones+=2;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DW \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           }else if (symbolTable.getType(identifier) == TokenType.STRING && !symbolTable.getSymbolEntry(identifier).getValue().equals("0")) {
               producciones+=8;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DB \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           } else if (symbolTable.getType(identifier) == TokenType.NUMBER) {
               producciones+=2;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DW \t ?\n");
           } else if (symbolTable.getType(identifier) == TokenType.STRING) {
               producciones+=8;
               sb.append(bitToHex(producciones)).append("\t").append(identifier).append("\t DB \t ?\n");
           }
        }

        int printCount = 0;
        for(int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).getType() == TokenType.PRINT){
                printCount++;
                producciones+=8;
                Token msgValue = tokens.get(i + 2);
                if(msgValue.getType() == TokenType.STRING){
                    sb.append(bitToHex(producciones)).append("\tmsg").append(printCount).append("\t DB \t ").append(msgValue.getLexeme()).append(", '$'\n");
                }else if(msgValue.getType() == TokenType.IDENTIFIER){
                    sb.append(bitToHex(producciones)).append("\tmsg").append(printCount).append("\t DB \t ").append(symbolTable.getSymbolEntry(msgValue.getLexeme()).getValue()).append(", '$'\n");
                } else if (msgValue.getType() == TokenType.NUMBER){
                    sb.append(bitToHex(producciones)).append("\tmsg").append(printCount).append("\t DB \t ").append(msgValue.getLexeme()).append(", '$'\n");
                }
            }
        }

        producciones = 0;
        printCount = 0;

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
            } else if(tokens.get(i).getType() == TokenType.PRINT){
                printCount++;
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \tDX,OFFSET ").append("msg").append(printCount).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \tAH, 9\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tINT \t21H\n");
            }
        }
        return sb.toString();
    }
}
