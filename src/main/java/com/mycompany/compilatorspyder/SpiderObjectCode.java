package com.mycompany.compilatorspyder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderObjectCode {
    private Map<String, String> instructionTable;
    private Map<String, String> registerTable;
    private Map<String, String> immediateValuesTable;
    private Map<String, String> variableAddressesTable;
    public static ArrayList<String> assemblyCode;

    public SpiderObjectCode(String intermediateCode) {
        instructionTable = new HashMap<>();
        registerTable = new HashMap<>();
        immediateValuesTable = new HashMap<>();
        variableAddressesTable = new HashMap<>();
        processDataSection(intermediateCode);
        System.out.println("Variable addresses table: " + variableAddressesTable);
        assemblyCode = convertStringToList(intermediateCode);
        initializeTables();
    }

    private void processDataSection(String intermediateCode) {
        Pattern pattern = Pattern.compile("\\.DATA\\n(.*?)\\.CODE", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(intermediateCode);
        if (matcher.find()) {
            String dataSection = matcher.group(1);
            String[] lines = dataSection.split("\\n");
            for (String line : lines) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 1) {
                    String variableName = parts[1];
                    String address = parts[0];
                    variableAddressesTable.put(variableName, convertAddressToBinary(address));
                }
            }
        }
    }

    private String convertAddressToBinary(String address) {
        int decimal = Integer.parseInt(address, 16);
        String binaryString = Integer.toBinaryString(decimal);
        binaryString = String.format("%16s", binaryString).replace(' ', '0');
        return flipHalfBinary(binaryString);
    }

    private String flipHalfBinary(String binaryString) {
        String high = binaryString.substring(0, 8);
        String low = binaryString.substring(8);
        return low + high;
    }

    private void initializeTables() {
        // Instruction table
        instructionTable.put("MOV1", "1011"); // MOV AX, <value>, MOV AH, <value>, MOV DX, <value>
        instructionTable.put("MOV2", "100010000000"); // MOV <register>, AX
        instructionTable.put("ADD", "00000011"); // ADD AX, <mem>
        instructionTable.put("SUB", "00010100"); // SUB AX, <value>
        instructionTable.put("INT", "11001101"); // INT 21H
        instructionTable.put("LEA", "1000110100"); // LEA DX, <value>
        // Register table
        registerTable.put("AX", "0000");
        registerTable.put("AX2", "10000110"); // mod 10 reg 000 r/m 110
        registerTable.put("BX", "0001");
        registerTable.put("CX", "0010");
        registerTable.put("DX", "0011");
        registerTable.put("DX2", "010");
        registerTable.put("SI", "0100");
        registerTable.put("DI", "0101");
        registerTable.put("SP", "0110");
        registerTable.put("BP", "0111");
        registerTable.put("AH", "1000");
        registerTable.put("BL", "0001");
        registerTable.put("DH", "1010");

        // Immediate values table
        immediateValuesTable.put("0", "00000000");
        immediateValuesTable.put("1", "00000001");
        immediateValuesTable.put("3", "00000011");
        immediateValuesTable.put("5", "00000101");
        immediateValuesTable.put("9", "00001001");
        immediateValuesTable.put("09H", "00001001");
        immediateValuesTable.put("21H", "00100001");
        // Add additional values as needed
    }

    public String translateLine(ArrayList<String> parts) {
        StringBuilder binaryCode = new StringBuilder();

        if (parts.get(1).equals("MOV")) {
            if (parts.get(2).equals("AX") || parts.get(2).equals("AH") || parts.get(2).equals("DX"))  {
                if (immediateValuesTable.containsKey(parts.get(3)) || variableAddressesTable.containsKey(parts.get(3))) {
                    binaryCode.append(instructionTable.get("MOV1"));
                    binaryCode.append(registerTable.get(parts.get(2)));
                    if (immediateValuesTable.containsKey(parts.get(3))) {
                        binaryCode.append(immediateValuesTable.get(parts.get(3)));
                    } else if (variableAddressesTable.containsKey(parts.get(3))) {
                        binaryCode.append(variableAddressesTable.get(parts.get(3)));
                    }
                } else {
                    throw new IllegalArgumentException("Immediate value or address not found: " + parts.get(3));
                }
            } else if (variableAddressesTable.containsKey(parts.get(2))) {
                binaryCode.append(instructionTable.get("MOV2"));
                binaryCode.append(variableAddressesTable.get(parts.get(2)));
                binaryCode.append(registerTable.get(parts.get(3)));
            }
        } else if (parts.get(1).equals("LEA")) {
            // Handle LEA instruction
            if (variableAddressesTable.containsKey(parts.get(3))) {
                binaryCode.append(instructionTable.get("LEA"));
                binaryCode.append(registerTable.get("DX2"));
                binaryCode.append("110"); // Add the value "110"
                binaryCode.append(variableAddressesTable.get(parts.get(3)));
            } else {
                throw new IllegalArgumentException("Variable not found in .DATA: " + parts.get(3));
            }
        }  else if(parts.get(1).equals("ADD")){
            binaryCode.append(instructionTable.get("ADD"));
            binaryCode.append(registerTable.get("AX2"));
            binaryCode.append(variableAddressesTable.get(parts.get(3)));
        } else if (parts.get(1).equals("SUB")) {
            binaryCode.append(instructionTable.get("SUB"));
            binaryCode.append(registerTable.get("AX2"));
            binaryCode.append(variableAddressesTable.get(parts.get(3)));
        } else if (instructionTable.containsKey(parts.get(1))) {
            binaryCode.append(instructionTable.get(parts.get(1)));
            for (int i = 2; i < parts.size(); i++) {
                if (registerTable.containsKey(parts.get(i))) {
                    binaryCode.append(registerTable.get(parts.get(i)));
                } else if (variableAddressesTable.containsKey(parts.get(i))) {
                    binaryCode.append(variableAddressesTable.get(parts.get(i)));
                } else if (immediateValuesTable.containsKey(parts.get(i))) {
                    binaryCode.append(immediateValuesTable.get(parts.get(i)));
                }
            }
        }

        return binaryCode.toString();
    }

    public String translateCode() {
        String SEG = "0000 : ";
        int OFFSET = 0;
        StringBuilder objectCode = new StringBuilder();
        ArrayList<String> parts = new ArrayList<>();
        boolean inCodeSection = false;

        for (String element : assemblyCode) {
            if (element.equals("CODE")) {
                inCodeSection = true;
                continue;
            }

            if (inCodeSection) {
                if (element.matches("\\d{4}")) {  // If the element is an address
                    if (!parts.isEmpty()) {  // Translate the accumulated instruction
                        System.out.println("Parts: " + parts);
                        String binaryCode = translateLine(parts);
                        System.out.println("Binary Code: " + binaryCode);
                        if (!binaryCode.isEmpty()) {
                            String formattedCode = formatBinary(binaryCode);
                            objectCode.append(String.format("%s%04X    %s%n", SEG, OFFSET, formattedCode));
                            OFFSET += binaryCode.length() / 8;
                        }
                        parts.clear();
                    }
                }
                parts.add(element);
            }
        }

        // Translate the last accumulated instruction
        if (!parts.isEmpty()) {
            String binaryCode = translateLine(parts);
            if (!binaryCode.isEmpty()) {
                String formattedCode = formatBinary(binaryCode);
                objectCode.append(String.format("%s%04X    %s%n", SEG, OFFSET, formattedCode));
            }
        }

        return objectCode.toString();
    }

    private String formatBinary(String binaryCode) {
        StringBuilder formattedCode = new StringBuilder();
        for (int i = 0; i < binaryCode.length(); i += 4) {
            if (i > 0) {
                formattedCode.append(" ");
            }
            formattedCode.append(binaryCode, i, Math.min(i + 4, binaryCode.length()));
        }
        return formattedCode.toString();
    }

    public static ArrayList<String> convertStringToList(String str) {
        ArrayList<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+\\s+)?(\\w+\\s+)(\\w+)(\\s+\\w+)?(\\s+.*)?");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null) {
                    for (String element : group.trim().split("\\s+")) {
                        list.add(element.replace(",", ""));
                    }
                }
            }
        }

        return list;
    }

    public static void main(String[] args) {
        String spyderIntermedio = """
                .DATA
                0002	x	 DW 	5
                0004	y	 DW 	3
                0006	z	 DW 	 ?
                000E	msg1	 DB 	 "Hola Mundo", '$'
                0016	msg2	 DB 	 "Adios Mundo", '$'
                .CODE
                0001		MOV 	AX, x
                0002		ADD 	AX, y
                0003		MOV 	z, AX
                0004		LEA 	DX, msg1
                0005		MOV 	AH, 09H
                0006		INT 	21H
                0007		LEA 	DX, msg2
                0008		MOV 	AH, 09H
                0009		INT 	21H""";

        SpiderObjectCode codigoObjeto = new SpiderObjectCode(spyderIntermedio);
        System.out.println(codigoObjeto.translateCode());
    }
}

