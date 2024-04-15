/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilatorspyder;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, SymbolEntry> symbolMap;

    public SymbolTable() {
        this.symbolMap = new HashMap<>();
    }

    public Map<String, SymbolEntry> getSymbolMap() {
        return symbolMap;
    }

    public void addSymbol(String identifier, SymbolEntry type) {
        symbolMap.put(identifier, type);
    }

    public boolean containsSymbol(String identifier) {
        return symbolMap.containsKey(identifier);
    }
    
    public SymbolEntry getSymbolEntry(String identifier){
        return symbolMap.get(identifier);
    }

    public TokenType getType(String identifier) {
        return symbolMap.get(identifier).getType();
    }
    
   @Override 
   public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Symbol Table:\n");
    for (Map.Entry<String, SymbolEntry> entry : symbolMap.entrySet()) {
        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
    }
    return sb.toString();
}
}


