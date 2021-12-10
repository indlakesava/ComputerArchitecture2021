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
        //Encoding the Opcodes to 6 bits.
	private String encodeOpcode(String s) {
		// Encodes the instruction in the opcode
		String encoded_opcode = "";
		switch (s) {
		case "LDR":
			encoded_opcode = "000001";
			break;
		case "LDA":
			encoded_opcode = "000011";
			break;
		case "LDX":
			encoded_opcode = "101001";
			break;
		case "STR":
			encoded_opcode = "000010";
			break;
		case "STX":
			encoded_opcode = "101010";
			break;
		case "JZ":
			encoded_opcode = "001010";
			break;
		case "JNE":
			encoded_opcode = "001011";
			break;
		case "JCC":
			encoded_opcode = "001100";
			break;
		case "JMA":
			encoded_opcode = "001101";
			break;
		case "JSR":
			encoded_opcode = "001110";
			break;
		case "RFS":
			encoded_opcode = "001111";
			break;
		case "SOB":
			encoded_opcode = "110000";
			break;
		case "JGE":
			encoded_opcode = "110001";
			break;
		case "AMR":
			encoded_opcode = "000100";
			break;
		case "SMR":
			encoded_opcode = "000101";
			break;
		case "AIR":
			encoded_opcode = "000110";
			break;
		case "SIR":
			encoded_opcode = "000111";
			break;
		case "MLT":
			encoded_opcode = "010100"; // 20
			break;
		case "DVD":
			encoded_opcode = "010101"; // 21
			break;
		case "TRR":
			encoded_opcode = "010110"; // 22
			break;
		case "AND":
			encoded_opcode = "010111"; // 23
			break;
		case "ORR":
			encoded_opcode = "011000"; // 24
			break;
		case "NOT":
			encoded_opcode = "011001"; // 25
			break;
		case "SRC":
			encoded_opcode = "011111"; // 31
			break;
		case "RRC":
			encoded_opcode = "100000"; // 32
			break;
                case "TRAP":
			encoded_opcode = "100100"; // 36
			break;
		case "IN":
			encoded_opcode = "111101"; // 61
			break;
		case "OUT":
			encoded_opcode = "111110"; // 62
			break;
		default:
                        Simulator.MFR = "0100";
			System.out.println("Invalid Instruction");
		}
		return encoded_opcode;
	}

        //decoding the Opcodes.
        public String decodeOpcode(String s) {
		// Decodes opcode to instruction
		String decoded_opcode = "";
		switch (s) {
		case "000001":
			decoded_opcode = "LDR";
			break;
		case "000011":
			decoded_opcode = "LDA";
			break;
		case "101001":
			decoded_opcode = "LDX";
			break;
		case "000010":
			decoded_opcode = "STR";
			break;
		case "101010":
			decoded_opcode = "STX";
			break;
		case "001010":
			decoded_opcode = "JZ";
			break;
		case "001011":
			decoded_opcode = "JNE";
			break;
		case "001100":
			decoded_opcode = "JCC";
			break;
		case "001101":
			decoded_opcode = "JMA";
			break;
		case "001110":
			decoded_opcode = "JSR";
			break;
		case "001111":
			decoded_opcode = "RFS";
			break;
		case "110000":
			decoded_opcode = "SOB";
			break;
		case "110001":
			decoded_opcode = "JGE";
			break;
		case "000100":
			decoded_opcode = "AMR";
			break;
		case "000101":
			decoded_opcode = "SMR";
			break;
		case "000110":
			decoded_opcode = "AIR";
			break;
		case "000111":
			decoded_opcode = "SIR";
			break;
		case "010100": // 20
			decoded_opcode = "MLT";
			break;
		case "010101": // 21
			decoded_opcode = "DVD";
			break;
		case "010110": // 22
			decoded_opcode = "TRR";
			break;
		case "010111": // 23
			decoded_opcode = "AND";
			break;
		case "011000": // 24
			decoded_opcode = "ORR";
			break;
		case "011001": // 25
			decoded_opcode = "NOT";
			break;
		case "011111": // 31
			decoded_opcode = "SRC";
			break;
		case "100000": // 32
			decoded_opcode = "RRC";
			break;
                case "100100": // 36
			decoded_opcode = "TRAP"; 
			break;
		case "111101": // 61
			decoded_opcode = "IN";
			break;
		case "111110": // 62
			decoded_opcode = "OUT";
			break;
		default:
			System.out.println("Invalid code");
		}
		return decoded_opcode;
	}

        //Convert Hexadecimal to Binary of 16 bits
	public String hexToBin16(String s) {
		// Converts Hexadecimal value to Binary format
		// Also appends zeroes to make it 16 bit
		if (s == null) {
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

        //Convert Hexadecimal to Binary of 5 bits
	public String hexToBin5(String s) {
		// Converts Hexadecimal value to Binary format
		// Also appends zeroes to make it 5 bit
		if (s == null) {
			return "00000";
		}

		String bin = new BigInteger(s, 5).toString(2);
		if (bin.length() == 5) {
			return bin;
		}

		StringBuilder sb = new StringBuilder();
		while (sb.length() < 5 - bin.length()) {
			sb.append('0');
		}
		sb.append(bin);

		return sb.toString();
	}

        //Convert Binary to Hexadecimal
	public String binToHex(String s) {
		// Converts Binary value to Hexadecimal format
		int decimal = Integer.parseInt(s, 2);
		System.out.println("dec," + decimal);
		String hexStr = Integer.toString(decimal, 16);
		System.out.println("hexStr," + hexStr);
		return hexStr;
	}

        //Convert Hexadecimal to Binary
	public String hexToBin(String s) {
		// Converts Hexadecimal value to Binary format
		// Doen't append zeroes to make it 16 bit. Returns as it is.
		if (s == null) {
			s = "0";
		}
		return new BigInteger(s, 16).toString(2);
	}

        //Convert Hexadecimal to decimal
	public int hexToDec(String s) {
		// Converts Hexadecimal value to decimal format
		return Integer.parseInt(s, 16);
	}

        //Convert Decimal to Hexadecimal
	public String decToHex(int i) {
		// Converts Decimal to Hexadecimal format
		return Integer.toHexString(i);
	}

        //Convert Binary to Decimal
	public int binToDec(String s) {
		// Converts Binary value to Decimal format
		return Integer.parseInt(s, 2);
	}

        //Convert Decimal to Binary 
	public String decToBin(int i) {
		// Converts Decimal value to Binary format
		return Integer.toString(i, 2);
	}
        
        //Convert Decimal to Binary of 16 bits
        public String decToBin16(int i) {
		// Converts decimal value to Binary format
		// Also appends zeroes to make it 16 bit
		String bin = Integer.toString(i, 2);
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

        //Convert Decimal to Binary of 32 bits
	public String decToBin32(int i) {
		String bin = Integer.toString(i, 2);
		if (bin.length() == 32) {
			return bin;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < 32 - bin.length()) {
			sb.append('0');
		}
		sb.append(bin);
		return sb.toString();
	}

        //Encoding the Instruction
	public String instructionToWord(String op, String rem) {
		// Encodes instruction to word(2 bytes) data
		String instructionWord = "";
		String opcode = encodeOpcode(op);
		String R_IX_I_Add = encode_R_IX_I_Add(rem, op);
		return binToHex(opcode + R_IX_I_Add);
	}

        //Encoding the register
	private String encode_reg(String s_reg) {
		int reg = Integer.parseInt(s_reg);
		if (reg == 0) {
			return "00";
		} else if (reg == 1) {
			return "01";
		} else if (reg == 2) {
			return "10";
		} else if (reg == 3) {
			return "11";
		} else {
			Simulator.error = "Invalid Register";
			return "";
		}
	}
        
        //Encoding the trap register
	private String encode_trp(String s_trp) {
		int trp = Integer.parseInt(s_trp);
		if (trp == 0) {
			return "00";
		} else if (trp == 1) {
			return "01";
		} else if (trp == 2) {
			return "10";
		} else {
                        Simulator.MFR = "0010";
			Simulator.error = "Invalid Register";
			return "";
		}
	}

        //Decoding the register
	public String get_reg_val(String bin_reg) {

		if (bin_reg.equals("00")) {
			return Simulator.R0;
		} else if (bin_reg.equals("10")) {
			return Simulator.R2;
		} else if (bin_reg.equals("01")) {
			return Simulator.R1;
		} else if (bin_reg.equals("11")){
			return Simulator.R3;
		} else {
			return "";
		}

	}

        //Setting register value after multiplication
	public void set_reg_val_MLT(String bin_reg, int result) {
		int reg = Integer.parseInt(bin_reg, 2);
		int overflow = 0;
		String bin_result = "";
		if (result > 65535) {
			Simulator.CC = "1" + Simulator.CC.substring(1);
			overflow = 1;
		} else {
			Simulator.CC = "0" + Simulator.CC.substring(1);
		}
		if (reg == 0) {
			bin_result = decToBin32(result);
			Simulator.R0 = bin_result.substring(0, 16);
			Simulator.R1 = bin_result.substring(16, 32);

		} else if (reg == 2) {
			bin_result = decToBin32(result);
			Simulator.R2 = bin_result.substring(0, 16);
			Simulator.R3 = bin_result.substring(16, 32);
		}
	}

        //Setting register value after division
	public void set_reg_val_DVD(String bin_reg, int rx, int ry) {
		int reg = Integer.parseInt(bin_reg, 2);
		int DIVZERO = 0;
		if (ry == 0) {
			DIVZERO = 1;
			Simulator.CC = Simulator.CC.substring(0,2) + "1" + Simulator.CC.substring(3);
		}else {
			Simulator.CC = Simulator.CC.substring(0, 2) + "0" + Simulator.CC.substring(3);
			if (reg == 0) {
				Simulator.R0 = decToBin16(rx / ry);
				Simulator.R1 = decToBin16(rx % ry);
			} else if (reg == 2) {
				Simulator.R2 = decToBin16(rx / ry);
				Simulator.R3 = decToBin16(rx % ry);
			}
		}
	}

        //Setting register value
	public void output_to_reg(String reg, String result) {
		if (reg.equals("00")) {
			Simulator.R0 = result;
		} else if (reg.equals("01")) {
			Simulator.R1 = result;
		} else if (reg.equals("10")) {
			Simulator.R2 = result;
		} else if (reg.equals("11")) {
			Simulator.R3 = result;
		}
	}

        //Encoding Index registers
	private String encode_ix(String s_ix) {
		int ix = Integer.parseInt(s_ix);
		if (ix == 0) {
			return "00";
		} else if (ix == 1) {
			return "01";
		} else if (ix == 2) {
			return "10";
		} else if (ix == 3) {
			return "11";
		} else {
			Simulator.error = "Invalid Index Register";
			return "";
		}
	}

        //Encoding Inidrect addressing
	private String encode_i(String s_I) {
		int I = Integer.parseInt(s_I);
		if (I == 0) {
			return "0";
		} else if (I == 1) {
			return "1";
		} else {
			Simulator.error = "Invalid addressing";
			return "";
		}
	}

        //Encoding the address
	private String encode_address(String add) {
		String add_bin = hexToBin(add);
		if (add_bin.length() == 5) {
			return add_bin;
		} else if (add_bin.length() > 5) {
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

        //Encoding the Count value
	private String encode_count(String count) {
		String Count = decToBin(Integer.parseInt(count));
		switch (Count.length()){
			case 1:
				return "000" + Count;
			case 2:
				return "00" + Count;
			case 3:
				return "0" + Count;
			default:
				return Count;
		}
	}

        //Encoding the AL value
	private String encode_AL(String AL) {
		String al = decToBin(Integer.parseInt(AL));
		return al;
	}

        //Encoding the LR value
	private String encode_LR(String LR) {
		String lr = decToBin(Integer.parseInt(LR));
		return lr;
	}

        //Encoding the Register, Index register, Immediate register, and address
	private String encode_R_IX_I_Add(String s, String operation) {
		// Encodes General Purpose Register, Index register, Indirect addressing,
		// Addressing to bits
		StringBuilder bin = new StringBuilder();
		String[] splitted = s.split(",");

		if (operation.equals("LDR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("LDA")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("LDX")) {
			bin.append("00");
			bin.append(encode_ix(splitted[0]));
			if (splitted.length == 3) {
				bin.append(encode_i(splitted[1]));
				bin.append(encode_address(splitted[2]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[1]));
			}
		} else if (operation.equals("STR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("STX")) {
			bin.append("00");
			bin.append(encode_ix(splitted[0]));
			if (splitted.length == 3) {
				bin.append(encode_i(splitted[1]));
				bin.append(encode_address(splitted[2]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[1]));
			}
		} else if (operation.equals("JZ")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("JNE")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("JCC")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("JMA")) {// CHECK
			bin.append("00");
			bin.append(encode_ix(splitted[0]));
			if (splitted.length == 3) {
				bin.append(encode_i(splitted[1]));
				bin.append(encode_address(splitted[2]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[1]));
			}
		} else if (operation.equals("JSR")) {// CHECK
			bin.append("00");
			bin.append(encode_ix(splitted[0]));
			if (splitted.length == 3) {
				bin.append(encode_i(splitted[1]));
				bin.append(encode_address(splitted[2]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[1]));
			}
		} else if (operation.equals("RFS")) {// CHECK
			for(int i=0;i<10-(hexToBin(splitted[0])).length();i++) {
				bin.append("0");
			}
			bin.append(hexToBin(splitted[0]));
		} else if (operation.equals("SOB")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("JGE")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("AMR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("SMR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_ix(splitted[1]));
			if (splitted.length == 4) {
				bin.append(encode_i(splitted[2]));
				bin.append(encode_address(splitted[3]));
			} else {
				bin.append("0");
				bin.append(encode_address(splitted[2]));
			}
		} else if (operation.equals("AIR")) {// CHECK
			bin.append(encode_reg(splitted[0]));	
			bin.append("000");
			for(int i=0;i<5-(hexToBin(splitted[1])).length();i++) {
				bin.append("0");
			}
			bin.append(hexToBin(splitted[1]));
		} else if (operation.equals("SIR")) {// CHECK
			bin.append(encode_reg(splitted[0]));
			bin.append("000");
			for(int i=0;i<5-(hexToBin(splitted[1])).length();i++) {
				bin.append("0");
			}
			bin.append(hexToBin(splitted[1]));
		}else if(operation.equals("MLT")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_reg(splitted[1]));
			bin.append("000000");
		}else if(operation.equals("DVD")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_reg(splitted[1]));
			bin.append("000000");
		}else if(operation.equals("TRR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_reg(splitted[1]));
			bin.append("000000");
		}else if(operation.equals("AND")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_reg(splitted[1]));
			bin.append("000000");
		}else if(operation.equals("ORR")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_reg(splitted[1]));
			bin.append("000000");
		}else if(operation.equals("NOT")) {
			bin.append(encode_reg(splitted[0]));
			bin.append("00000000");
		}else if(operation.equals("SRC")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(encode_AL(splitted[3]));
			bin.append(encode_LR(splitted[2]));
			bin.append("00");
			bin.append(encode_count(splitted[1]));
		}else if(operation.equals("RRC")) {
			bin.append(encode_reg(splitted[0]));
			bin.append(splitted[3]);
			bin.append(splitted[2]);
			bin.append("00");
			bin.append(encode_count(splitted[1]));
		}else if(operation.equals("TRAP")) {
			bin.append(encode_trp(splitted[0]));
			bin.append("00000000");
                }else if(operation.equals("IN")) {
			bin.append(encode_reg(splitted[0]));
			bin.append("00000000");
		}else if(operation.equals("OUT")) {
			bin.append(encode_reg(splitted[0]));
			bin.append("00000000");
		}
		return bin.toString();
	}

	public String addHex(String s1, String s2) {
		// Adds 2 Hexadecimal values and returns decimal value
		int i1 = hexToDec(s1);
		int i2 = hexToDec(s2);
		return decToHex(i1 + i2);
	}

	public String addBin(String s1, String s2) {
		// Adds 2 Binary values and returns Binary value
		int i1 = binToDec(s1);
		int i2 = binToDec(s2);
		return decToBin(i1 + i2);
	}

	public String EffectiveAddress(String s) {
		// Method calculates effective address
                Cache cache_obj = new Cache();
		int IX = Integer.parseInt(s.substring(0, 2), 2);
		int I = Integer.parseInt(s.substring(2, 3), 2);
		int Add = Integer.parseInt(s.substring(3, 8), 2);
		String EA = "";

		String IX_Val = "";
		if (IX == 1) {
			IX_Val = Simulator.IX1;
		} else if (IX == 2) {
			IX_Val = Simulator.IX2;
		} else if (IX == 3) {
			IX_Val = Simulator.IX3;
		}

		if (I == 0) {
			if (IX == 0) {
				// NO indirect addressing and No Indexing
				EA = decToHex(Add);
			} else {
				// NO indirect addressing and Indexing
				EA = addHex(decToHex(Add), binToHex(IX_Val));
			}
		} else if (I == 1) {
			if (IX == 0) {
				// indirect addressing, but NO indexing
				EA = cache_obj.get_memory(Add);
			} else {
				// both indirect addressing and indexing
				EA = cache_obj.get_memory(hexToDec(addHex(decToHex(Add), binToHex(IX_Val))));
			}
		}
		return EA;
	}
}
