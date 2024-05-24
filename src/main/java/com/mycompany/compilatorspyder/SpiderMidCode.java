/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilatorspyder;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String bitToHex(int bits){
        BigInteger bigInteger = BigInteger.valueOf(bits);
        return String.format("%04X", bigInteger);
    }

    public String generateAssemblyIntelCode(){
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t.DATA\n");
        int producciones = 0;
        String direction = "";
        for (String identifier : symbolTable.getSymbolMap().keySet()) {
           if (symbolTable.getType(identifier) == TokenType.NUMBER && !symbolTable.getSymbolEntry(identifier).getValue().equals("0")) {
               producciones+=2;
               direction = bitToHex(producciones);
               sb.append(direction).append("\t").append(identifier).append("\t DW \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           }else if (symbolTable.getType(identifier) == TokenType.STRING && !symbolTable.getSymbolEntry(identifier).getValue().equals("0")) {
               producciones+=8;
               direction = bitToHex(producciones);
               sb.append(direction).append("\t").append(identifier).append("\t DB \t").append(symbolTable.getSymbolEntry(identifier).getValue()).append("\n");
           } else if (symbolTable.getType(identifier) == TokenType.NUMBER) {
               producciones+=2;
               direction = bitToHex(producciones);
               sb.append(direction).append("\t").append(identifier).append("\t DW \t ?\n");
           } else if (symbolTable.getType(identifier) == TokenType.STRING) {
               producciones+=8;
               direction = bitToHex(producciones);
               sb.append(direction).append("\t").append(identifier).append("\t DB \t ?\n");
           }
        }

        int printCount = 0;
        for(int i = 0; i < tokens.size(); i++){
            if(tokens.get(i).getType() == TokenType.PRINT){
                printCount++;
                producciones+=8;
                direction = bitToHex(producciones);
                Token msgValue = tokens.get(i + 2);
                String msg = "msg" + printCount;
                String value = "";
                if(msgValue.getType() == TokenType.STRING){
                    value = msgValue.getLexeme();
                    sb.append(direction).append("\t").append(msg).append("\t DB \t ").append(value).append(", '$'\n");
                }else if(msgValue.getType() == TokenType.IDENTIFIER){
                    value = symbolTable.getSymbolEntry(msgValue.getLexeme()).getValue();
                    sb.append(direction).append("\t").append(msg).append("\t DB \t ").append(value).append(", '$'\n");
                } else if (msgValue.getType() == TokenType.NUMBER){
                    value = msgValue.getLexeme();
                    sb.append(direction).append("\t").append(msg).append("\t DB \t ").append(value).append(", '$'\n");
                }
                symbolTable.addSymbol(msg, new SymbolEntry(TokenType.STRING, value));

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
                sb.append(bitToHex(producciones)).append("\t").append("\tLEA \tDX, ").append("msg").append(printCount).append("\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tMOV \tAH, 09H\n");
                producciones+=1;
                sb.append(bitToHex(producciones)).append("\t").append("\tINT \t21H\n");
            }
        }
        return sb.toString();
    }
}
