package com.mycompany.compilatorspyder;

import javax.swing.SwingUtilities;

/**
 *
 * @author EduTQ
            * 
            *Correcto
            block Test{
                x := 5;
                y := 3;
                z := x + y;
                print("Hola Mundo");
                print("Adios Mundo");
            }
            *Error Semantico
            block Test{
                x := 5;
                y := 3;
                z := x + y;
                w := "String";
                a := w + 1;
           }
           *Error Sintactico
           block Test{
                x := 5;
                y := 3;
                z := x + y;
                a := ;
           }
 
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompilerUI gui = new CompilerUI();
            gui.setVisible(true);
        });
    }
}
