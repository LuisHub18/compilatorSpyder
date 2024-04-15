/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compilatorspyder;
/**
 *
 * @author EduTQ
 */
public class SymbolEntry {
    private TokenType type;
    private String value;

    public SymbolEntry(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "Type: " + type + ", Value: " + value;
    }
}
