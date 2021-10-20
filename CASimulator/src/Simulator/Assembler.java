/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulator;

import java.math.BigInteger;

/**
 *
 * @author indla
 */
public class Assembler {
    private String encodeOpcode(String s){ 
        //Encodes the instruction in the opcode
        String encoded_opcode = "";
        switch (s) {
            case "LDR":  encoded_opcode = "000001";
                     break;
            case "LDA":  encoded_opcode = "000011";
                     break;
            case "LDX":  encoded_opcode = "101001";
                     break;
            case "STR":  encoded_opcode = "000010";
                     break;
            case "STX":  encoded_opcode = "101010";
                     break;
            default: System.out.println("Invalid Instruction");
        }
        return encoded_opcode;
    }
    
    public String decodeOpcode(String s){  
        //Decodes opcode to instruction
        String decoded_opcode = "";
        switch (s) {
            case "000001":  decoded_opcode = "LDR";
                     break;
            case "000011":  decoded_opcode = "LDA";
                     break;
            case "101001":  decoded_opcode = "LDX";
                     break;
            case "000010":  decoded_opcode = "STR";
                     break;
            case "101010":  decoded_opcode = "STX";
                     break;
            default: System.out.println("Invalid code");
        }
        return decoded_opcode;
    }
    
    public String hexToBin16(String s) {
        //Converts Hexadecimal value to Binary format
        //Also appends zeroes to make it 16 bit
        if(s==null){
            return "0000000000000000";
        }
        
        String bin = new BigInteger(s, 16).toString(2);
        if (bin.length() == 16) {
            return bin;
        }
        
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 16 - bin.length()) {
            sb.append('0');
        }
        sb.append(bin);

        return sb.toString();
    } 
    
    public String binToHex(String s) {
        //Converts Binary value to Hexadecimal format
        int decimal = Integer.parseInt(s,2);
        System.out.println("dec,"+decimal);
        String hexStr = Integer.toString(decimal,16);
        System.out.println("hexStr,"+hexStr);
        return hexStr;
    }
    
    public String hexToBin(String s) {
        //Converts Hexadecimal value to Binary format
        //Doen't append zeroes to make it 16 bit. Returns as it is.
        if(s==null){
            s = "0";
        }
        return new BigInteger(s, 16).toString(2);
    }
    
    public int hexToDec(String s){
        //Converts Hexadecimal value to decimal format
        return Integer.parseInt(s,16);
    }
    
    public String decToHex(int i){
        //Converts Decimal to Hexadecimal format
        return Integer.toHexString(i);
    }
    
    public int binToDec(String s){
        //Converts Binary value to Decimal format
        return Integer.parseInt(s, 2);
    }
    
    public String decToBin(int i){
        //Converts Decimal value to Binary format
        return Integer.toString(i, 2);
    }
    
    public String instructionToWord(String op, String rem) {
        //Encodes instruction to word(2 bytes) data
        String instructionWord = "";
        String opcode = encodeOpcode(op);
        String R_IX_I_Add = encode_R_IX_I_Add(rem, op);
        return binToHex(opcode + R_IX_I_Add);
    }
    
    private String encode_reg(String s_reg)
    {
        int reg = Integer.parseInt(s_reg);
        if (reg==0) {
            return "00";
        } else if (reg==1) {
            return "01";
        } else if (reg==2) {
            return "10";
        } else if (reg==3){
            return "11";
        } else {
            Simulator.error = "Invalid Register";
            return "";
        }
    }
    
    private String encode_ix(String s_ix)
    {
        int ix = Integer.parseInt(s_ix);
        if (ix==0) {
            return "00";
        } else if (ix==1) {
            return "01";
        } else if (ix==2) {
            return "10";
        } else if (ix== 3) {
            return "11";
        } else {
            Simulator.error = "Invalid Index Register";
            return "";
        }
    }
    
    private String encode_i(String s_I)
    {
        int I = Integer.parseInt(s_I);
        if (I==0) {
            return "0";
        } else if (I==1) {
            return "1";
        } else {
            Simulator.error = "Invalid addressing";
            return "";
        }
    }

    private String encode_address(String add)
    {
        String add_bin = hexToBin(add);
        if (add_bin.length() == 5) {
            return add_bin;
        } else if (add_bin.length() > 5){
            Simulator.error = "Address exceeding 32";
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            while (sb.length() < 5 - add_bin.length()) {
                sb.append('0');
            }
            sb.append(add_bin);
            return sb.toString();
        }
    }
    
    private String encode_R_IX_I_Add(String s, String operation) {
        //Encodes General Purpose Register, Index register, Indirect addressing, Addressing to bits
        StringBuilder bin = new StringBuilder();
        String[] splitted = s.split(",");
        
        if(operation.equals("LDR"))
        {
            bin.append(encode_reg(splitted[0]));
            bin.append(encode_ix(splitted[1]));
            bin.append(encode_i(splitted[2]));
            bin.append(encode_address(splitted[3]));
        }
        else if(operation.equals("LDA"))
        {
            bin.append(encode_reg(splitted[0]));
            bin.append(encode_ix(splitted[1]));
            bin.append(encode_i(splitted[2]));
            bin.append(encode_address(splitted[3]));
        }
        else if(operation.equals("LDX"))
        {
            bin.append("00");
            bin.append(encode_ix(splitted[0]));
            bin.append(encode_i(splitted[1]));
            bin.append(encode_address(splitted[2]));
        }
        else if(operation.equals("STR"))
        {
            bin.append(encode_reg(splitted[0]));
            bin.append(encode_ix(splitted[1]));
            bin.append(encode_i(splitted[2]));
            bin.append(encode_address(splitted[3]));
        }
        else if(operation.equals("STX"))
        {
            bin.append("00");
            bin.append(encode_ix(splitted[0]));
            bin.append(encode_i(splitted[1]));
            bin.append(encode_address(splitted[2]));
        }
        
        return bin.toString();
    }
    
    public String addHex(String s1, String s2){
        //Adds 2 Hexadecimal values and returns decimal value
        int i1 = hexToDec(s1);
        int i2 = hexToDec(s2);
        return decToHex(i1+i2);
    }
    
    public String addBin(String s1, String s2){
        //Adds 2 Binary values and returns Binary value
        int i1 = binToDec(s1);
        int i2 = binToDec(s2);
        return decToBin(i1+i2);
    }
    
    public String EffectiveAddress(String s)
    {
        //Method calculates effective address
        int IX= Integer.parseInt(s.substring(0, 2), 2);
        int I = Integer.parseInt(s.substring(2,3), 2);
        int Add = Integer.parseInt(s.substring(3, 8), 2);
        String EA = "";
        
        String IX_Val = "";
        if (IX==1) {
            IX_Val = Simulator.IX1;
        } else if (IX==2) {
            IX_Val = Simulator.IX2;
        } else if (IX== 3) {
            IX_Val = Simulator.IX3;
        }
        
        if(I==0){
            if(IX==0){
                // NO indirect addressing and No Indexing
                EA = decToHex(Add);
            }
            else{
                // NO indirect addressing and Indexing
                EA = addHex(decToHex(Add), binToHex(IX_Val));
            }
        } else if(I==1){
            if(IX==0){ 
                // indirect addressing, but NO indexing
                EA = Simulator.memory[Add];
            }
            else{
                // both indirect addressing and indexing
                EA = Simulator.memory[hexToDec(addHex(decToHex(Add), binToHex(IX_Val)))];
            }
        }
        return EA;
    }
}
