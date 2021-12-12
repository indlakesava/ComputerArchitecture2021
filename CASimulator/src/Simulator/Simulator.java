/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.Arrays;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;

/**
 *
 * @author indla
 */

class FP{
	float realFP; //contain real FP no. to perform add, sub
	String FPR; //contain FP 16 bit string to show on simulator
	public FP() {
		realFP=0.0f;
		FPR="0000000000000000";
		
	}
}


public class Simulator extends javax.swing.JFrame {

	/**
	 * Creates new form Home
	 */
	public static String error = "";
	public static String[] memory = new String[4096];
	public static HashMap<String, String[]> cache_d = new HashMap<>();
	public static Queue<String> cache_q = new LinkedList<>();
	public static String R0 = "0000000000000000";
	public static String R1 = "0000000000000000";
	public static String R2 = "0000000000000000";
	public static String R3 = "0000000000000000";
	public static String IX1 = "0000000000000000";
	public static String IX2 = "0000000000000000";
	public static String IX3 = "0000000000000000";
	public static String PC = "000000100000";
	public static String MAR = "000000000000";
	public static String MBR = "0000000000000000";
	public static String IR = "0000000000000000";
	public static String MFR = "0000";
	public static String CC = "0000";
	public static FP FR0 = new FP();
	public static FP FR1 = new FP();
        public String IN = "";
        private int sentences_len = 0;
        private int word_len = 0;
        private String word = "";

	DefaultTableModel memory_model;
	DefaultTableModel cache_model;

        //constructor
	public Simulator() {
		initComponents();
		memory_model = (DefaultTableModel) tblMemory.getModel();
		cache_model = (DefaultTableModel) tblCache.getModel();
		loadComponents();
		setTblMemory();
		setTblCache();
                setTrap();
	}

        //Loads all the components when simulator is started
	private void loadComponents() {
		txtPC.setEditable(false);
		txtR0.setEditable(false);
		txtR1.setEditable(false);
		txtR2.setEditable(false);
		txtR3.setEditable(false);
		txtIXR1.setEditable(false);
		txtIXR2.setEditable(false);
		txtIXR3.setEditable(false);
		txtMAR.setEditable(false);
		txtMBR.setEditable(false);
		txtIR.setEditable(false);
		txtMFR.setEditable(false);
		txtCC.setEditable(false);
		txtFR0.setEditable(false);
		txtFR1.setEditable(false);
		// txtOpCode.setEditable(false);
		// txtGPR.setEditable(false);
		// txtIXR12.setEditable(false);
		// txtI.setEditable(false);
		// txtAddress.setEditable(false);
		txtarea_instructions.setEditable(false);
		memory[0] = "0000000000000000";
		txtPC.setText(PC);
	}

        //This method sets the table memory to display the memory interms of table in the Simulator
	private void setTblMemory() {
		try {
			Assembler assembler_obj = new Assembler();
			memory_model.setRowCount(0);
			for (int i = 0; i < memory.length; i++) {
				if (memory[i] != null) {
					String loc_hex = assembler_obj.decToHex(i);
					String loc = assembler_obj.hexToBin16(loc_hex) + "(" + loc_hex + ")";
					String data = assembler_obj.hexToBin16(memory[i]);
					memory_model.addRow(new Object[] { loc, data });
				}
			}
		} catch (Exception ex) {
			txtarea_instructions.setText("Error Loading Memory Table");
		}
	}
        
        //Declaring Trap enums with method names 
        private enum Trap {
            read_sentences, get_word, search_word
        }
        
        //Initializing the Trap details
        private void setTrap()
        {
                Assembler assembler_obj = new Assembler();
                Simulator.memory[0] = assembler_obj.binToHex("0000000111000010");
                Simulator.memory[450] = assembler_obj.binToHex(assembler_obj.decToBin16(Trap.read_sentences.ordinal()));
                Simulator.memory[451] = assembler_obj.binToHex(assembler_obj.decToBin16(Trap.get_word.ordinal()));
                Simulator.memory[452] = assembler_obj.binToHex(assembler_obj.decToBin16(Trap.search_word.ordinal()));
        }
        
        //First trap method that is used to read 6 sentences
        private void read_sentences(){
                String sentences = JOptionPane.showInputDialog(this, "Enter the paragraph (6 sentences)");
                txtarea_instructions.setText(sentences);
                Assembler assembler_obj = new Assembler();
                memory[499] = "20";
                int start_loc = 500;
                for (char c: sentences.toCharArray())
                {
                        memory[start_loc + sentences_len] = assembler_obj.binToHex(assembler_obj.decToBin16((int)c));
                        sentences_len += 1;
                }
        }
        
        //Second trap method that is used to read 1 word
        private void get_word(){
                word = JOptionPane.showInputDialog(this, "Enter the word to be searched");
                Assembler assembler_obj = new Assembler();
                int start_loc = 4000;
                for (char c: word.toCharArray())
                {
                        memory[start_loc + word_len] = assembler_obj.binToHex(assembler_obj.decToBin16((int)c));
                        word_len += 1;
                }
        }
        
        //Third trap method that is used to search the word in the sentences
        private void search_word(){
            int current_sentence = 1;
            int word_number = 1;
            int found = 0;
            String[] sentence_ends = new String[] {"2e", "3f", "21"};
            
            int temp = 0;
            for(int i=0; i<sentences_len; i++)
            {
                if(Arrays.stream(sentence_ends).anyMatch(memory[500+i]::equals))
                {
                    current_sentence += 1;
                    word_number = 0;
                }

                if(memory[500+i].equals("20"))
                {
                    word_number += 1;
                }
                
                if((memory[500+i-1].equals("20")) && (memory[500+i].equals(memory[4000])))
                {
                    int remaining_word_len = word_len;
                    int flag = 1;
                    while(remaining_word_len>0)
                    {
                        if(!memory[500+i+word_len-remaining_word_len].equals(memory[4000+word_len-remaining_word_len]))
                        {
                            flag = 0;
                            break;
                        }
                        remaining_word_len -= 1;
                    }
                    
                    if(flag==1)
                    {
                        if((memory[500+i+word_len] == null) || (memory[500+i+word_len] != null && memory[500+i+word_len].equals("20")))
                        {
                            JOptionPane.showMessageDialog(this, "Word is: "+word+"\nSentence number is: "+current_sentence+
                                    "\nWord number in the sentence is: "+word_number);
                            found = 1;
                            break;

                        }
                    }
                }
            }
            
            if(found==0)
            {
                JOptionPane.showMessageDialog(this, "Unable to find matching word");
            }
        }

        //This method sets the table cache to display the cache interms of table in the Simulator
	public void setTblCache() {
		try {
			Assembler assembler_obj = new Assembler();
			cache_model.setRowCount(0);
			for (String s : cache_q) {
				Vector row = new Vector();
				row.add(s);
				for (String i : cache_d.get(s)) {
					row.add(assembler_obj.hexToBin16(i));
				}
				cache_model.addRow(row);
			}
		} catch (Exception ex) {
			txtarea_instructions.setText("Error Loading Cache Table");
		}
	}

        //Method which rund on each cycle
	private void cycle() {
		// Method that keeps track of PC, MAR, MBR, IR and passes instruction further if
		// we didn't reach the end of instruction set
		try {
			Assembler assembler_obj = new Assembler();
			Cache cache_obj = new Cache();
			setTblCache();
			Simulator.MAR = Simulator.PC;
			txtMAR.setText(Simulator.MAR);
			Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
			txtPC.setText(Simulator.PC);
			Simulator.MBR = assembler_obj.hexToBin16(cache_obj.get_memory(assembler_obj.binToDec(Simulator.MAR)));
			txtMBR.setText(Simulator.MBR);
			Simulator.IR = Simulator.MBR;
			txtIR.setText(Simulator.IR);
			if (!Simulator.IR.equals("0000000000000000")) {
				single_instruction(Simulator.IR);
                                setTblMemory();
                                update_registers();
			} else {
				txtarea_instructions.setText("Halt reached");
				Simulator.PC = Simulator.MAR;
			}
			setTblCache();
		} catch (Exception ex) {
			txtarea_instructions.setText("Error running instruction");
		}
	}
        
        //This method updates registers values in the simulator
	public void update_registers(){
		txtR0.setText(R0);
		txtR1.setText(R1);
		txtR2.setText(R2);
		txtR3.setText(R3);
		txtCC.setText(CC);
        txtMFR.setText(MFR);
		txtFR0.setText(FR0.FPR);
		txtFR1.setText(FR1.FPR);
	}

        //Method which decode the instruction and runs its respective tasks.
	private void single_instruction(String instruction) {
		// Function that decodes the instruction and then runs that correspondIng
		// instruction
		Assembler assembler_obj = new Assembler();
		Cache cache_obj = new Cache();
		String ins = assembler_obj.decodeOpcode(instruction.substring(0, 6));
		txtOpCode.setText(instruction.substring(0, 6));
		String Reg = instruction.substring(6, 8);
		txtGPR.setText(instruction.substring(6, 8));
		String IX = instruction.substring(8, 10);
		txtIXR12.setText(instruction.substring(8, 10));
		String Indirect = instruction.substring(10, 11);
		txtIndirect.setText(instruction.substring(10, 11));
		String Add = instruction.substring(11, 16);
		txtAddress.setText(instruction.substring(11, 16));
		String EA, mem, res, reg_val;
		int mem_loc;

		switch (ins) {
		case "JZ":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				res = "";
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					res = R0;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					res = R1;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					res = R2;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					res = R3;
				}
				if (assembler_obj.binToDec(res) == 0) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
					txtPC.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(Simulator.PC)));
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "JNE":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				res = "";
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					res = R0;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					res = R1;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					res = R2;
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					res = R3;
				}
				if (assembler_obj.binToDec(res) != 0) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
					txtPC.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(Simulator.PC)));
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "JCC":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
				txtarea_instructions.setText("Memory limit exceed"); // Memory limit exceeds for EA to be assigned to PC
			} else {
				res = "";
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					res = CC.substring(0, 1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					res = CC.substring(1, 2);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					res = CC.substring(2, 3);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					res = CC.substring(3);
				}
				if (Integer.parseInt(res) == 1) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
					txtPC.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(Simulator.PC)));
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "JMA":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				res = assembler_obj.hexToBin16(mem);
				Simulator.PC = assembler_obj.hexToBin16(EA);
				txtPC.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(Simulator.PC)));
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(IX) 
                                + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "JSR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				res = assembler_obj.hexToBin16(EA);
				R3 = assembler_obj.addBin(Simulator.PC, "1");
				R3 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R3)));
				Simulator.PC = res;
				txtPC.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(Simulator.PC)));
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(IX) 
                                + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "RFS":
			Simulator.PC = R3;
			txtPC.setText(Simulator.PC);
			int immed = assembler_obj.binToDec(instruction.substring(6, instruction.length()));
			R0 = assembler_obj.decToBin16(immed);
			txtR0.setText(R0);
			txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(R0));
			break;
		case "SOB":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				int cOr = 0;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOr = assembler_obj.binToDec(R0);
					R0 = assembler_obj.decToBin16(cOr - 1);
					R0 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R0)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cOr = assembler_obj.binToDec(R1);
					R1 = assembler_obj.decToBin16(cOr - 1);
					R1 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R1)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cOr = assembler_obj.binToDec(R2);
					R2 = assembler_obj.decToBin16(cOr - 1);
					R2 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R2)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cOr = assembler_obj.binToDec(R3);
					R3 = assembler_obj.decToBin16(cOr - 1);
					R3 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R3)));
				}

				if (cOr > 0) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
				}
				else // cOr is less than zero we have to set the underflow bit
					Simulator.CC = Simulator.CC.substring(0,1) + "1" + Simulator.CC.substring(2);
				update_registers();
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "JGE":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// PC
			} else {
				int cOr = 0;

				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOr = assembler_obj.binToDec(R0);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cOr = assembler_obj.binToDec(R1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cOr = assembler_obj.binToDec(R2);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cOr = assembler_obj.binToDec(R3);
				}

				if (cOr >= 0) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
					txtPC.setText(Simulator.PC);
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "AMR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				int cOfEA = assembler_obj.hexToDec(mem);
				int cOfR=0;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOfR = assembler_obj.binToDec(R0);
					R0 = assembler_obj.decToBin(cOfEA + cOfR);
					R0 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R0)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cOfR = assembler_obj.binToDec(R1);
					R1 = assembler_obj.decToBin(cOfEA + cOfR);
					R1 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R1)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cOfR = assembler_obj.binToDec(R2);
					R2 = assembler_obj.decToBin(cOfEA + cOfR);
					R2 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R2)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cOfR = assembler_obj.binToDec(R3);
					R3 = assembler_obj.decToBin(cOfEA + cOfR);
					R3 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R3)));
				}
				if (cOfR<0)
				   Simulator.CC = "01" + Simulator.CC.substring(2);
				else if (cOfR > 65535)
				   Simulator.CC = "10"+Simulator.CC.substring(2);
				else
					Simulator.CC = "00"+Simulator.CC.substring(2);
				update_registers();
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "SMR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				int cOfEA = assembler_obj.hexToDec(mem);
				int cOfR=0;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOfR = assembler_obj.binToDec(R0);
					R0 = assembler_obj.decToBin(cOfR - cOfEA);
					R0 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R0)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cOfR = assembler_obj.binToDec(R1);
					R1 = assembler_obj.decToBin(cOfR - cOfEA);
					R1 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R1)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cOfR = assembler_obj.binToDec(R2);
					R2 = assembler_obj.decToBin(cOfR - cOfEA);
					R2 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R2)));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cOfR = assembler_obj.binToDec(R3);
					R3 = assembler_obj.decToBin(cOfR - cOfEA);
					R3 =(assembler_obj.hexToBin16(assembler_obj.binToHex(R3)));
				}
				if (cOfR<0)
					   Simulator.CC = "01" + Simulator.CC.substring(2);
					else if (cOfR > 65535)
					   Simulator.CC = "10"+Simulator.CC.substring(2);
					else
						Simulator.CC = "00"+Simulator.CC.substring(2);
					update_registers();
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "AIR":
			int cOfR=0;
			immed = assembler_obj.binToDec(instruction.substring(8, instruction.length()));
			if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
				cOfR = assembler_obj.binToDec(R0);
				R0 = assembler_obj.decToBin16(cOfR + immed);
				txtR0.setText(R0);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
				cOfR = assembler_obj.binToDec(R1);
				R1 = assembler_obj.decToBin16(cOfR + immed);
				txtR0.setText(R1);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
				cOfR = assembler_obj.binToDec(R2);
				R2 = assembler_obj.decToBin16(cOfR + immed);
				txtR0.setText(R2);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
				cOfR = assembler_obj.binToDec(R3);
				R3 = assembler_obj.decToBin16(cOfR + immed);
				txtR0.setText(R3);
			}
			if (cOfR<0)
				   Simulator.CC = "01" + Simulator.CC.substring(2);
				else if (cOfR > 65535)
				   Simulator.CC = "10"+Simulator.CC.substring(2);
				else
					Simulator.CC = "00"+Simulator.CC.substring(2);
				update_registers();
			txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
					+  assembler_obj.decToHex(immed));
			break;
		case "SIR":
			cOfR=0;
			immed = assembler_obj.binToDec(instruction.substring(8, instruction.length()));
			if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
				cOfR = assembler_obj.binToDec(R0);
				R0 = assembler_obj.decToBin16(cOfR - immed);
				txtR0.setText(R0);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
				cOfR = assembler_obj.binToDec(R1);
				R1 = assembler_obj.decToBin16(cOfR - immed);
				txtR0.setText(R1);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
				cOfR = assembler_obj.binToDec(R2);
				R2 = assembler_obj.decToBin16(cOfR - immed);
				txtR0.setText(R2);
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
				cOfR = assembler_obj.binToDec(R3);
				R3 = assembler_obj.decToBin16(cOfR - immed);
				txtR0.setText(R3);
			}
			if (cOfR<0)
				   Simulator.CC = "01" + Simulator.CC.substring(2);
				else if (cOfR > 65535)
				   Simulator.CC = "10"+Simulator.CC.substring(2);
				else
					Simulator.CC = "00"+Simulator.CC.substring(2);
				update_registers();
			txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
					+  assembler_obj.decToHex(immed));
			break;
		case "LDR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				res = assembler_obj.hexToBin16(mem);

				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					R0 = res;
					txtR0.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					R1 = res;
					txtR1.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					R2 = res;
					txtR2.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					R3 = res;
					txtR3.setText(res);
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "LDA":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");// Memory limit exceeds for EA to be assigned to
																		// // PC
			} else {
				res = assembler_obj.hexToBin16(EA);
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					R0 = res;
					txtR0.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					R1 = res;
					txtR1.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					R2 = res;
					txtR2.setText(res);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					R3 = res;
					txtR3.setText(res);
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "LDX":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (Integer.parseInt(IX) == 0) {
				txtarea_instructions.setText("Index register can't be 0 in this case");
			} else {
				if (assembler_obj.hexToDec(EA) > 4095) {
                                        MFR = "1000";
					txtarea_instructions.setText("Memory limit exceeded");
				} else {
					mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
					res = assembler_obj.hexToBin16(mem);
					if (Integer.parseInt(IX) == java.lang.Integer.parseInt("01")) {
						IX1 = res;
						txtIXR1.setText(res);
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("10")) {
						IX2 = res;
						txtIXR2.setText(res);
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("11")) {
						IX3 = res;
						txtIXR3.setText(res);
					}
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(IX) 
                                + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "STR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			mem_loc = assembler_obj.hexToDec(EA);
			if (mem_loc > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.R0));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.R1));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.R2));
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.R3));
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "STX":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (Integer.parseInt(IX) == 0) {
				txtarea_instructions.setText("Index register can't be 0 in this case");
			} else {
				mem_loc = assembler_obj.hexToDec(EA);
				if (mem_loc > 4095) {
                                        MFR = "1000";
					txtarea_instructions.setText("Memory limit exceeded");
				} else {
					if (Integer.parseInt(IX) == java.lang.Integer.parseInt("01")) {
						cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.IX1));
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("10")) {
						cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.IX2));
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("11")) {
						cache_obj.set_memory(mem_loc, assembler_obj.binToHex(Simulator.IX3));
					}
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(IX) 
                                + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "MLT": // 20
			int rx_MLT = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(6, 8)));
			int ry_MLT = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(8, 10)));
			assembler_obj.set_reg_val_MLT(Reg, rx_MLT * ry_MLT);
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(8, 10)));
			break;
		case "DVD": // 21
			int rx_DVD = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(6, 8)));
			int ry_DVD = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(8, 10)));
			assembler_obj.set_reg_val_DVD(Reg, rx_DVD, ry_DVD);
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(8, 10)));
			break;
		case "TRR": // 22
			int rx_TRR = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(6, 8)));
			int ry_TRR = assembler_obj.binToDec(assembler_obj.get_reg_val(instruction.substring(8, 10)));
			if (rx_TRR == ry_TRR) {
				Simulator.CC = Simulator.CC.substring(0,3) + "1" ;
			} else {
				Simulator.CC = Simulator.CC.substring(0,3) + "0" ;
			}
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(8, 10)));
			break;
		case "AND": // 23
			String rx_AND = assembler_obj.get_reg_val(instruction.substring(6, 8));
			String ry_AND = assembler_obj.get_reg_val(instruction.substring(8, 10));
			char[] rx_AND_ARR = rx_AND.toCharArray();
			char[] ry_AND_ARR = ry_AND.toCharArray();
			char[] temp_AND = new char[16];
			for (int i = 0; i < 16; i++) {
				if (rx_AND_ARR[i] == '1' & ry_AND_ARR[i] == '1') {
					temp_AND[i] = '1';
				} else {
					temp_AND[i] = '0';
				}
			}
			String result_AND = String.valueOf(temp_AND);
			assembler_obj.output_to_reg(instruction.substring(6, 8), result_AND);
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(8, 10)));
			break;
		case "ORR": // 24
			String rx_ORR = assembler_obj.get_reg_val(instruction.substring(6, 8));
			String ry_ORR = assembler_obj.get_reg_val(instruction.substring(8, 10));
			char[] rx_ORR_ARR = rx_ORR.toCharArray();
			char[] ry_ORR_ARR = ry_ORR.toCharArray();
			char[] temp_ORR = new char[16];
			for (int i = 0; i < 16; i++) {
				if (rx_ORR_ARR[i] == '1' || ry_ORR_ARR[i] == '1') {
					temp_ORR[i] = '1';
				} else {
					temp_ORR[i] = '0';
				}
			}
			String result_ORR = String.valueOf(temp_ORR);
			assembler_obj.output_to_reg(instruction.substring(6, 8), result_ORR);
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(8, 10)));
			break;
		case "NOT": // 25
			String rx_NOT = assembler_obj.get_reg_val(instruction.substring(6, 8));

			char[] rx_NOT_ARR = rx_NOT.toCharArray();
			char[] temp_NOT = new char[16];
			for (int i = 0; i < 16; i++) {
				if (rx_NOT_ARR[i] == '1') {
					temp_NOT[i] = '0';
				} else {
					temp_NOT[i] = '1';
				}
			}
			String result_NOT = String.valueOf(temp_NOT);
			assembler_obj.output_to_reg(instruction.substring(6, 8), result_NOT);
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg));
			break;
		case "SRC": // 31
			String content_SRC = assembler_obj.get_reg_val(instruction.substring(6, 8));
			int AL_SRC = assembler_obj.binToDec(instruction.substring(8, 9));
			int LR_SRC = assembler_obj.binToDec(instruction.substring(9, 10));
			int count_SRC = assembler_obj.binToDec(instruction.substring(12));
                        String Zeroes = "0000000000000000";
                        String Ones = "1111111111111111";
			if (count_SRC == 0 || count_SRC > 15) {
				break;
			}
			if (AL_SRC == 1) {
				// logically
				if (LR_SRC == 1) {
					// logically left
                	reg_val = content_SRC.substring(count_SRC) + Zeroes.substring(0, count_SRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				} else {
					// logically right
					reg_val = Zeroes.substring(16-count_SRC) + content_SRC.substring(0,16-count_SRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				}
			} else {
				// arithetically
				if (LR_SRC == 1) {
					// arithetically left
					reg_val = content_SRC.substring(0, 1) + content_SRC.substring(1+count_SRC) + Zeroes.substring(0, count_SRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				} else {
					// arithetically right
                                        if(content_SRC.substring(0, 1).equals("1")){
                                                reg_val = content_SRC.substring(0, 1) + Ones.substring(16-count_SRC) + content_SRC.substring(1,16-count_SRC);
                                                assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
                                        }
                                        else if(content_SRC.substring(0, 1).equals("0")){
                                                reg_val = content_SRC.substring(0, 1) + Zeroes.substring(16-count_SRC) + content_SRC.substring(1,16-count_SRC);
                                                assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
                                        }
				}
			}
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(12)) + "," + assembler_obj.binToHex(instruction.substring(8, 9)) 
                                +"," + assembler_obj.binToHex(instruction.substring(8, 9)));
			break;

		case "RRC": // 32
			String content_RRC = assembler_obj.get_reg_val(instruction.substring(6, 8));
			int AL_RRC = assembler_obj.binToDec(instruction.substring(8, 9));
			int LR_RRC = assembler_obj.binToDec(instruction.substring(9, 10));
			int count_RRC = assembler_obj.binToDec(instruction.substring(12));
			if (count_RRC == 0 || count_RRC > 15) {
				break;
			}

			if (AL_RRC == 1) {
				// logically
				if (LR_RRC == 1) {
					// logically left
					reg_val = content_RRC.substring(count_RRC) + content_RRC.substring(0, count_RRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				} else {
					// logically right
					reg_val = content_RRC.substring(16-count_RRC) + content_RRC.substring(0,16-count_RRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				}
			} else {
				// arithetically
				if (LR_RRC == 1) {
					// arithetically left
					reg_val = content_RRC.substring(0, 1) + content_RRC.substring(1+count_RRC) + content_RRC.substring(1, 1+count_RRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				} else {
					// arithetically right
					reg_val = content_RRC.substring(0, 1) + content_RRC.substring(16-count_RRC) + content_RRC.substring(1,16-count_RRC);
					assembler_obj.output_to_reg(instruction.substring(6, 8), reg_val);
				}
			}
			update_registers();
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(instruction.substring(12)) + "," + assembler_obj.binToHex(instruction.substring(8, 9)) 
                                +"," + assembler_obj.binToHex(instruction.substring(8, 9)));
			break;
		case "TRAP": // 36
			txtarea_instructions.setText("Test");
			if(Reg.equals("00"))
			{
				read_sentences();
			}
			else if (Reg.equals("01"))
			{
				get_word();
			}
			else if (Reg.equals("10"))
			{
				search_word();
			}
			txtarea_instructions.setText("Executed Instruction:\n"+ ins + " " + String.valueOf(assembler_obj.binToDec(Reg)));
			break;
		case "IN": // 61
			// input from GUI to register
			String val = JOptionPane.showInputDialog(this, "Enter a Value");
			assembler_obj.output_to_reg(Reg, assembler_obj.decToBin16(Integer.parseInt(val)));
			txtarea_instructions.setText("Executed Instruction:\n"+ ins + " " + String.valueOf(assembler_obj.binToDec(Reg)) + ",0");
			break;
		case "OUT": // 62
			// out put from register to GUI
			String content_OUT = assembler_obj.get_reg_val(instruction.substring(6, 8));
			//txtarea_instructions.setText("Out Value Is: "+String.valueOf(assembler_obj.binToDec(content_OUT)));
			JOptionPane.showMessageDialog(this, "Out Value Is: "+String.valueOf(assembler_obj.binToDec(content_OUT)));
                        break;
		case "FADD":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				float cOfEA = assembler_obj.hexToFloat(mem);
				float cOfFR=0.0f;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOfFR = FR0.realFP;
					cOfFR=cOfEA + cOfFR;
					FR0.realFP = cOfFR;
					FR0.FPR= assembler_obj.floatTo16bitString(FR0.realFP);
					txtFR0.setText(FR0.FPR);
				} else {
					cOfFR = FR1.realFP;
					cOfFR=cOfEA + cOfFR;
					FR1.realFP = cOfFR;
					FR1.FPR= assembler_obj.floatTo16bitString(FR1.realFP);
					txtFR1.setText(FR1.FPR);
				}
				if (cOfFR<0)
				   Simulator.CC = "01" + Simulator.CC.substring(2);
				else if (cOfFR > 65535)
				   Simulator.CC = "10"+Simulator.CC.substring(2);
				else
					Simulator.CC = "00"+Simulator.CC.substring(2);
				update_registers();
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "FSUB":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				float cOfEA = assembler_obj.hexToFloat(mem);
				float cOfFR=0.0f;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOfFR = FR0.realFP;
					cOfFR= cOfFR-cOfEA;
					FR0.realFP = cOfFR;
					FR0.FPR= assembler_obj.floatTo16bitString(FR0.realFP);
					txtFR0.setText(FR0.FPR);
				} else {
					cOfFR = FR1.realFP;
					cOfFR= cOfFR-cOfEA;
					FR1.realFP = cOfFR;
					FR1.FPR= assembler_obj.floatTo16bitString(FR1.realFP);
					txtFR1.setText(FR1.FPR);
				}
				if (cOfFR<0)
				   Simulator.CC = "01" + Simulator.CC.substring(2);
				else if (cOfFR > 65535)
				   Simulator.CC = "10"+Simulator.CC.substring(2);
				else
					Simulator.CC = "00"+Simulator.CC.substring(2);
				update_registers();
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "LDFR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				res = assembler_obj.floatTo16bitString(assembler_obj.hexToFloat(mem));

				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					FR0.FPR = res;
					FR0.realFP=assembler_obj.hexToFloat(mem);
					txtFR0.setText(res);
				} else {
					FR1.FPR = res;
					FR1.realFP=assembler_obj.hexToFloat(mem);
					txtFR1.setText(res);
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "STFR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			mem_loc = assembler_obj.hexToDec(EA);
			if (mem_loc > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cache_obj.set_memory(mem_loc, assembler_obj.floatToHex(FR0.realFP));
				} else {
					cache_obj.set_memory(mem_loc, assembler_obj.floatToHex(FR1.realFP));
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "CNVRT":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
                                MFR = "1000";
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = cache_obj.get_memory(assembler_obj.hexToDec(EA));
				cOfR=0;
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					cOfR = assembler_obj.binToDec(R0);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					cOfR = assembler_obj.binToDec(R1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					cOfR = assembler_obj.binToDec(R2);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					cOfR = assembler_obj.binToDec(R3);
				}
				if (cOfR==1) {
					FR0.realFP = (float)assembler_obj.hexToDec(mem);
					FR0.FPR= assembler_obj.floatTo16bitString(FR0.realFP);
					txtFR0.setText(FR0.FPR);
				} else {
					cOfR = (int)assembler_obj.hexToFloat(mem);
					if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
						R0 = assembler_obj.decToBin(cOfR);
						R0=(assembler_obj.hexToBin16(assembler_obj.binToHex(R0)));
					} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
						R1 = assembler_obj.decToBin(cOfR);
						txtR1.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(R1)));
					} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
						R2 = assembler_obj.decToBin(cOfR);
						txtR2.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(R2)));
					} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
						R3 = assembler_obj.decToBin(cOfR);
						txtR3.setText(assembler_obj.hexToBin16(assembler_obj.binToHex(R3)));
					}
				}
			}
                        txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
			break;
		case "VADD": //opcode(6bit)+fr(2bit)+IX(2bit)+I(1bit)+Address(5bit)
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			float cOfFR_VADD = 0;
			if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
				cOfFR_VADD = FR0.realFP;
			} else  {
				cOfFR_VADD = FR1.realFP;
			}
			int vector1_VADD = Integer.parseInt(cache_obj.get_memory(assembler_obj.hexToDec(EA))); //vector1 start point
			String vector2_VADD_EA = assembler_obj.decToHex(assembler_obj.hexToDec(EA)+1);
			int vector2_VADD = Integer.parseInt(cache_obj.get_memory(assembler_obj.hexToDec(vector2_VADD_EA))); //vector2 start point
			for(int i=0; i < cOfFR_VADD; i++){
				int number1 = assembler_obj.hexToDec(cache_obj.get_memory(assembler_obj.hexToDec(Integer.toString(vector1_VADD + i))));
				int number2 = assembler_obj.hexToDec(cache_obj.get_memory(assembler_obj.hexToDec(Integer.toString(vector2_VADD + i))));
				int result = number1 + number2;
				String bin_result = assembler_obj.decToBin16(result);
				cache_obj.set_memory(assembler_obj.hexToDec(Integer.toString(vector1_VADD+i)), assembler_obj.binToHex(bin_result));
			}
			break;

		case "VSUB": //fr(2bit)+IX(2bit)+I(1bit)+Address(5bit)
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			float cOfFR_VSUB = 0;
			if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
				cOfFR_VSUB = FR0.realFP;
			} else  {
				cOfFR_VSUB = FR1.realFP;
			}
			int vector1_VSUB = Integer.parseInt(cache_obj.get_memory(assembler_obj.hexToDec(EA))); //vector1 start point
			String vector2_VSUB_EA = assembler_obj.decToHex(assembler_obj.hexToDec(EA)+1);
			int vector2_VSUB = Integer.parseInt(cache_obj.get_memory(assembler_obj.hexToDec(vector2_VSUB_EA))); //vector2 start point
			for(int i=0; i < cOfFR_VSUB; i++){
				int number1 = assembler_obj.hexToDec(cache_obj.get_memory(assembler_obj.hexToDec(Integer.toString(vector1_VSUB + i))));
				int number2 = assembler_obj.hexToDec(cache_obj.get_memory(assembler_obj.hexToDec(Integer.toString(vector2_VSUB + i))));
				int result = number1 - number2;
				String bin_result = assembler_obj.decToBin16(result);
				cache_obj.set_memory(assembler_obj.hexToDec(Integer.toString(vector1_VSUB+i)), assembler_obj.binToHex(bin_result));
			}
			break;
		default:
			break;
		}
	}

        //This method is invoked when a LD button is clicked
	private void LD_operation(String reg) {
		int flag = 1;

		if (txtOpCode.getText().length() != 6) {
			flag = 0;
		}
		if (txtGPR.getText().length() != 2) {
			flag = 0;
		}
		if (txtIXR12.getText().length() != 2) {
			flag = 0;
		}
		if (txtIndirect.getText().length() != 1) {
			flag = 0;
		}
		if (txtAddress.getText().length() != 5) {
			flag = 0;
		}

		if (flag == 1) {
			StringBuilder bin = new StringBuilder();

			if (reg.equals("PC") || reg.equals("MAR")) {
				bin.append(txtOpCode.getText().substring(4));
			} else {
				bin.append(txtOpCode.getText());
			}

			bin.append(txtGPR.getText());
			bin.append(txtIXR12.getText());
			bin.append(txtIndirect.getText());
			bin.append(txtAddress.getText());

			switch (reg) {
			case "R0":
				txtR0.setText(bin.toString());
				R0 = bin.toString();
				break;
			case "R1":
				txtR1.setText(bin.toString());
				R1 = bin.toString();
				break;
			case "R2":
				txtR2.setText(bin.toString());
				R2 = bin.toString();
				break;
			case "R3":
				txtR3.setText(bin.toString());
				R3 = bin.toString();
				break;
			case "PC":
				txtPC.setText(bin.toString());
				PC = bin.toString();
				break;
			case "MAR":
				txtMAR.setText(bin.toString());
				MAR = bin.toString();
				break;
			case "MBR":
				txtMBR.setText(bin.toString());
				MBR = bin.toString();
				break;
			case "FR0":
				txtFR0.setText(bin.toString());
				FR0.FPR = bin.toString();
				break;
			case "FR1":
				txtFR1.setText(bin.toString());
				FR1.FPR = bin.toString();
				break;
			}
		} else {
			txtarea_instructions
					.setText("Please check the number of bits for each field in instruction format at the bottom");
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblR0 = new javax.swing.JLabel();
        lblR1 = new javax.swing.JLabel();
        lblR2 = new javax.swing.JLabel();
        lblR4 = new javax.swing.JLabel();
        lblIXR1 = new javax.swing.JLabel();
        lblIXR2 = new javax.swing.JLabel();
        lblIXR3 = new javax.swing.JLabel();
        lblPC = new javax.swing.JLabel();
        lblMAR = new javax.swing.JLabel();
        lblMBR = new javax.swing.JLabel();
        lblIR = new javax.swing.JLabel();
        lblMFR = new javax.swing.JLabel();
        lblFR0 = new javax.swing.JLabel();
        lblFR1 = new javax.swing.JLabel();
        txtR0 = new javax.swing.JTextField();
        txtR1 = new javax.swing.JTextField();
        txtR2 = new javax.swing.JTextField();
        txtR3 = new javax.swing.JTextField();
        txtIXR1 = new javax.swing.JTextField();
        txtIXR2 = new javax.swing.JTextField();
        txtIXR3 = new javax.swing.JTextField();
        txtPC = new javax.swing.JTextField();
        txtMAR = new javax.swing.JTextField();
        txtMBR = new javax.swing.JTextField();
        txtIR = new javax.swing.JTextField();
        txtMFR = new javax.swing.JTextField();        
        txtFR0 = new javax.swing.JTextField();        
        txtFR1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtarea_instructions = new javax.swing.JTextArea();
        btn_IPL = new javax.swing.JButton();
        btn_SI = new javax.swing.JButton();
        btn_Run = new javax.swing.JButton();
        btn_Load = new javax.swing.JButton();
        btn_Store = new javax.swing.JButton();
        lblOpcode = new javax.swing.JLabel();
        lblGPR = new javax.swing.JLabel();
        lblIXR = new javax.swing.JLabel();
        lblImmediate = new javax.swing.JLabel();
        lblI1 = new javax.swing.JLabel();
        txtOpCode = new javax.swing.JTextField();
        txtGPR = new javax.swing.JTextField();
        txtIndirect = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtIXR12 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMemory = new javax.swing.JTable();
        btn_LD_R1 = new javax.swing.JButton();
        btn_LD_R0 = new javax.swing.JButton();
        btn_LD_R2 = new javax.swing.JButton();
        btn_LD_R3 = new javax.swing.JButton();
        btn_LD_PC = new javax.swing.JButton();
        btn_LD_MAR = new javax.swing.JButton();
        btn_LD_MBR = new javax.swing.JButton();
        btn_LD_FR0 = new javax.swing.JButton();
        btn_LD_FR1 = new javax.swing.JButton();

        lblCC = new javax.swing.JLabel();
        txtCC = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCache = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Computer Architecture Simulator");

        lblR0.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblR0.setText("R0");

        lblR1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblR1.setText("R1");

        lblR2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblR2.setText("R2");

        lblR4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblR4.setText("R3");

        lblIXR1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIXR1.setText("IXR1");

        lblIXR2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIXR2.setText("IXR2");

        lblIXR3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIXR3.setText("IXR3");

        lblPC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPC.setText("PC");

        lblMAR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblMAR.setText("MAR");

        lblMBR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblMBR.setText("MBR");

        lblIR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIR.setText("IR");

        lblMFR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblMFR.setText("MFR");

        lblFR0.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFR0.setText("FR0");

        lblFR1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFR1.setText("FR1");

        txtR0.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtR0.setText("0000000000000000");

        txtR1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtR1.setText("0000000000000000");

        txtR2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtR2.setText("0000000000000000");

        txtR3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtR3.setText("0000000000000000");

        txtIXR1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIXR1.setText("0000000000000000");

        txtIXR2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIXR2.setText("0000000000000000");

        txtIXR3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIXR3.setText("0000000000000000");

        txtPC.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPC.setText("000000000000");

        txtMAR.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMAR.setText("000000000000");

        txtMBR.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMBR.setText("0000000000000000");

        txtIR.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIR.setText("0000000000000000");

        txtMFR.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMFR.setText("0000");
        
        txtFR0.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFR0.setText("0000000000000000");

        txtFR1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFR1.setText("0000000000000000");
        

        txtarea_instructions.setColumns(20);
        txtarea_instructions.setLineWrap(true);
        txtarea_instructions.setRows(5);
        jScrollPane2.setViewportView(txtarea_instructions);

        btn_IPL.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_IPL.setText("IPL");
        btn_IPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_IPLActionPerformed(evt);
            }
        });

        btn_SI.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_SI.setText("SI");
        btn_SI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SIActionPerformed(evt);
            }
        });

        btn_Run.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_Run.setText("RUN");
        btn_Run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_RunActionPerformed(evt);
            }
        });

        btn_Load.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_Load.setText("LOAD");
        btn_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LoadActionPerformed(evt);
            }
        });

        btn_Store.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_Store.setText("STORE");
        btn_Store.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_StoreActionPerformed(evt);
            }
        });

        lblOpcode.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblOpcode.setText("OpCode");

        lblGPR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblGPR.setText("GPR");

        lblIXR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblIXR.setText("IXR");

        lblImmediate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblImmediate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImmediate.setText("I");

        lblI1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblI1.setText("Address");

        txtOpCode.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOpCode.setText("000000");

        txtGPR.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtGPR.setText("00");

        txtIndirect.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIndirect.setText("0");

        txtAddress.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtAddress.setText("00000");

        txtIXR12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtIXR12.setText("00");

        tblMemory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Location", "Data"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMemory.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblMemory.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblMemory);

        btn_LD_R1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_R1.setText("LD");
        btn_LD_R1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_R1ActionPerformed(evt);
            }
        });

        btn_LD_R0.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_R0.setText("LD");
        btn_LD_R0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_R0ActionPerformed(evt);
            }
        });

        btn_LD_R2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_R2.setText("LD");
        btn_LD_R2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_R2ActionPerformed(evt);
            }
        });

        btn_LD_R3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_R3.setText("LD");
        btn_LD_R3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_R3ActionPerformed(evt);
            }
        });

        btn_LD_FR0.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_FR0.setText("LD");
        btn_LD_FR0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_FR0ActionPerformed(evt);
            }
        });

        btn_LD_FR1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_FR1.setText("LD");
        btn_LD_FR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_FR1ActionPerformed(evt);
            }
        });


        btn_LD_PC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_PC.setText("LD");
        btn_LD_PC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_PCActionPerformed(evt);
            }
        });

        btn_LD_MAR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_MAR.setText("LD");
        btn_LD_MAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_MARActionPerformed(evt);
            }
        });

        btn_LD_MBR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_LD_MBR.setText("LD");
        btn_LD_MBR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LD_MBRActionPerformed(evt);
            }
        });

        lblCC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblCC.setText("CC");

        txtCC.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtCC.setText("0000");

        tblCache.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tag", "00", "01", "10", "11"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCache.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblCache.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblCache);


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblPC, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtPC, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_PC))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblIXR3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtIXR3, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblIXR2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtIXR2, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblR2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtR2, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_R2))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblR1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtR1, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_R1))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblR4, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtR3, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_R3))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblIXR1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtIXR1, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblR0, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtR0, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_R0))
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(lblMAR, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        							.addGap(18)
        							.addComponent(txtMAR, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btn_LD_MAR)))
        					.addGap(18)
        					.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE)
        					.addGap(18)
        					.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(btn_Run, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_IPL, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_SI, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_Store, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_Load, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addGroup(layout.createParallelGroup(Alignment.LEADING)
        							.addGroup(layout.createSequentialGroup()
        								.addComponent(lblMBR, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        								.addGap(18)
        								.addComponent(txtMBR, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        								.addPreferredGap(ComponentPlacement.RELATED)
        								.addComponent(btn_LD_MBR))
        							.addGroup(layout.createSequentialGroup()
        								.addGroup(layout.createParallelGroup(Alignment.LEADING)
        									.addComponent(lblIR, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        									.addComponent(lblCC, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        									.addComponent(lblFR0)
        									.addComponent(lblMFR, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        									.addComponent(txtOpCode, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
        									.addComponent(lblOpcode))
        								.addGroup(layout.createParallelGroup(Alignment.LEADING)
        									.addGroup(layout.createParallelGroup(Alignment.LEADING)
        										.addGroup(layout.createParallelGroup(Alignment.LEADING)
        											.addGroup(layout.createSequentialGroup()
        												.addGap(10)
        												.addComponent(txtGPR, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.RELATED)
        												.addComponent(txtIXR12, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.UNRELATED)
        												.addComponent(txtIndirect, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.UNRELATED)
        												.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
        											.addGroup(layout.createSequentialGroup()
        												.addGap(8)
        												.addComponent(lblGPR)
        												.addPreferredGap(ComponentPlacement.RELATED)
        												.addComponent(lblIXR)
        												.addPreferredGap(ComponentPlacement.UNRELATED)
        												.addComponent(lblImmediate, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.UNRELATED)
        												.addComponent(lblI1))
        											.addGroup(layout.createSequentialGroup()
        												.addComponent(txtFR0, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.RELATED)
        												.addComponent(btn_LD_FR0, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
        											.addGroup(layout.createSequentialGroup()
        												.addComponent(txtFR1, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        												.addPreferredGap(ComponentPlacement.RELATED)
        												.addComponent(btn_LD_FR1, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
        											.addComponent(txtIR, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
        										.addComponent(txtCC, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))
        									.addComponent(txtMFR, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE))))
        						.addComponent(lblFR1))
        					.addGap(18)
        					.addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)))
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(16)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblR0)
        						.addComponent(txtR0, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_LD_R0))
        					.addGap(7)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblR1)
        						.addComponent(txtR1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_LD_R1))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblR2)
        						.addComponent(txtR2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_LD_R2))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblR4)
        						.addComponent(txtR3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_LD_R3))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(txtIXR1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblIXR1))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblIXR2)
        						.addComponent(txtIXR2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        					.addGap(11)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblIXR3)
        						.addComponent(txtIXR3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        					.addGap(14)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(txtPC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblPC)
        						.addComponent(btn_LD_PC))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(txtMAR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblMAR)
        						.addComponent(btn_LD_MAR))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(txtMBR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblMBR)
        						.addComponent(btn_LD_MBR))
        					.addGap(0, 6, Short.MAX_VALUE)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(txtIR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(lblIR))
        					.addGap(11)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblMFR)
        						.addComponent(txtMFR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblCC)
        						.addComponent(txtCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblFR0)
        						.addComponent(txtFR0, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
        						.addComponent(btn_LD_FR0, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        						.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        							.addComponent(lblFR1)
        							.addComponent(txtFR1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
        						.addComponent(btn_LD_FR1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(lblOpcode)
        						.addComponent(lblGPR)
        						.addComponent(lblIXR)
        						.addComponent(lblImmediate)
        						.addComponent(lblI1))
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        						.addComponent(txtOpCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(txtGPR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(txtIXR12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(txtIndirect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        						.addComponent(jScrollPane1, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(btn_IPL, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.UNRELATED)
        							.addComponent(btn_SI, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.UNRELATED)
        							.addComponent(btn_Run, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.UNRELATED)
        							.addComponent(btn_Load, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.UNRELATED)
        							.addComponent(btn_Store, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        							.addGap(0, 21, Short.MAX_VALUE)))
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);

        btn_IPL.getAccessibleContext().setAccessibleName("");
        btn_SI.getAccessibleContext().setAccessibleName("");
        btn_Run.getAccessibleContext().setAccessibleName("");
        btn_Load.getAccessibleContext().setAccessibleName("");
        btn_Store.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void btn_IPLActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_IPLActionPerformed
		// Asks for the txt file to be read and once we select the ipl.txt file then it
		// reads the file and loads into memory
		Functionalities f_obj = new Functionalities();
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(selectedFile));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				String all = sb.toString();
				txtarea_instructions.setText(all);
				f_obj.set_memory(all);
				setTblMemory();
			} catch (Exception e) {
				txtarea_instructions.setText(e.toString());
			}
		}
	}// GEN-LAST:event_btn_IPLActionPerformed

	private void btn_RunActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_RunActionPerformed
		// Code that runs when Run button is clicked
		cycle();
		while (!Simulator.IR.equals("0000000000000000")) {
			cycle();
		}
	}// GEN-LAST:event_btn_RunActionPerformed

	private void btn_SIActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_SIActionPerformed
		// // Code that runs when SI button is clicked
		cycle();
	}// GEN-LAST:event_btn_SIActionPerformed

	private void btn_LD_R0ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R0ActionPerformed
		// TODO add your handling code here:
		LD_operation("R0");
	}// GEN-LAST:event_btn_LD_R0ActionPerformed

	private void btn_LD_R1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R1ActionPerformed
		// TODO add your handling code here:
		LD_operation("R1");
	}// GEN-LAST:event_btn_LD_R1ActionPerformed

	private void btn_LD_R2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R2ActionPerformed
		// TODO add your handling code here:
		LD_operation("R2");
	}// GEN-LAST:event_btn_LD_R2ActionPerformed

	private void btn_LD_R3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R3ActionPerformed
		// TODO add your handling code here:
		LD_operation("R3");
	}// GEN-LAST:event_btn_LD_R3ActionPerformed

	private void btn_LD_PCActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_PCActionPerformed
		// TODO add your handling code here:
		LD_operation("PC");
	}// GEN-LAST:event_btn_LD_PCActionPerformed

	private void btn_LD_MARActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_MARActionPerformed
		// TODO add your handling code here:
		LD_operation("MAR");
	}// GEN-LAST:event_btn_LD_MARActionPerformed

	private void btn_LD_MBRActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_MBRActionPerformed
		// TODO add your handling code here:
		LD_operation("MBR");
	}// GEN-LAST:event_btn_LD_MBRActionPerformed
	
	private void btn_LD_FR0ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R0ActionPerformed
		// TODO add your handling code here:
		LD_operation("FR0");
	}// GEN-LAST:event_btn_LD_FR0ActionPerformed
	
	private void btn_LD_FR1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LD_R0ActionPerformed
		// TODO add your handling code here:
		LD_operation("FR1");
	}// GEN-LAST:event_btn_LD_FR1ActionPerformed

	private void btn_StoreActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_StoreActionPerformed
		// TODO add your handling code here:
		Assembler assembler_obj = new Assembler();
		Cache cache_obj = new Cache();
		cache_obj.set_memory(assembler_obj.binToDec(txtMAR.getText()), assembler_obj.binToHex(txtMBR.getText()));
		setTblMemory();
	}// GEN-LAST:event_btn_StoreActionPerformed

	private void btn_LoadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LoadActionPerformed
		// TODO add your handling code here:
		Assembler assembler_obj = new Assembler();
		Cache cache_obj = new Cache();
		String ins = assembler_obj.hexToBin16(cache_obj.get_memory(assembler_obj.binToDec(txtMAR.getText())));
		txtOpCode.setText(ins.substring(0, 6));
		txtGPR.setText(ins.substring(6, 8));
		txtIXR12.setText(ins.substring(8, 10));
		txtIndirect.setText(ins.substring(10, 11));
		txtAddress.setText(ins.substring(11, 16));
	}// GEN-LAST:event_btn_LoadActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Simulator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Simulator().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_IPL;
    private javax.swing.JButton btn_LD_MAR;
    private javax.swing.JButton btn_LD_MBR;
    private javax.swing.JButton btn_LD_PC;
    private javax.swing.JButton btn_LD_R0;
    private javax.swing.JButton btn_LD_R1;
    private javax.swing.JButton btn_LD_R2;
    private javax.swing.JButton btn_LD_R3;
    private javax.swing.JButton btn_LD_FR0;
    private javax.swing.JButton btn_LD_FR1;
    private javax.swing.JButton btn_Load;
    private javax.swing.JButton btn_Run;
    private javax.swing.JButton btn_SI;
    private javax.swing.JButton btn_Store;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCC;
    private javax.swing.JLabel lblFR0;
    private javax.swing.JLabel lblGPR;
    private javax.swing.JLabel lblI1;
    private javax.swing.JLabel lblIR;
    private javax.swing.JLabel lblIXR;
    private javax.swing.JLabel lblIXR1;
    private javax.swing.JLabel lblIXR2;
    private javax.swing.JLabel lblIXR3;
    private javax.swing.JLabel lblImmediate;
    private javax.swing.JLabel lblMAR;
    private javax.swing.JLabel lblMBR;
    private javax.swing.JLabel lblMFR;
    private javax.swing.JLabel lblOpcode;
    private javax.swing.JLabel lblPC;
    private javax.swing.JLabel lblR0;
    private javax.swing.JLabel lblR1;
    private javax.swing.JLabel lblR2;
    private javax.swing.JLabel lblR4;
    private javax.swing.JLabel lblFR1;
    public javax.swing.JTable tblCache;
    private javax.swing.JTable tblMemory;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCC;
    private javax.swing.JTextField txtGPR;
    private javax.swing.JTextField txtIR;
    private javax.swing.JTextField txtIXR1;
    private javax.swing.JTextField txtIXR12;
    private javax.swing.JTextField txtIXR2;
    private javax.swing.JTextField txtIXR3;
    private javax.swing.JTextField txtIndirect;
    private javax.swing.JTextField txtMAR;
    private javax.swing.JTextField txtMBR;
    private javax.swing.JTextField txtMFR;
    private javax.swing.JTextField txtOpCode;
    private javax.swing.JTextField txtPC;
    private javax.swing.JTextField txtR0;
    private javax.swing.JTextField txtR1;
    private javax.swing.JTextField txtR2;
    private javax.swing.JTextField txtR3;
    private javax.swing.JTextArea txtarea_instructions;
    private javax.swing.JTextField txtFR0;
    private javax.swing.JTextField txtFR1;
    // End of variables declaration//GEN-END:variables
}
