/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author indla
 */
public class Simulator extends javax.swing.JFrame {

	/**
	 * Creates new form Home
	 */
	public static String error = "";
	public static String[] memory = new String[4096];
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

	DefaultTableModel model;

	public Simulator() {
		initComponents();
		model = (DefaultTableModel) tblMemory.getModel();
		loadComponents();
		setTblMemory();
	}

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
		// txtOpCode.setEditable(false);
		// txtGPR.setEditable(false);
		// txtIXR12.setEditable(false);
		// txtI.setEditable(false);
		// txtAddress.setEditable(false);
		txtarea_instructions.setEditable(false);
		memory[0] = "0000000000000000";
		txtPC.setText(PC);
	}

	private void setTblMemory() {
		try {
			Assembler assembler_obj = new Assembler();
			model.setRowCount(0);
			for (int i = 0; i < memory.length; i++) {
				if (memory[i] != null) {
					String loc = assembler_obj.decToHex(i);
					String data = assembler_obj.hexToBin16(memory[i]);
					model.addRow(new Object[] { loc, data });
				}
			}
		} catch (Exception ex) {
			txtarea_instructions.setText("Error Loading Memory Table");
		}
	}

	private void cycle() {
		// Method that keeps track of PC, MAR, MBR, IR and passes instruction further if
		// we didn't reach the end of instruction set
		try {
			Assembler assembler_obj = new Assembler();
			Simulator.MAR = Simulator.PC;
			txtMAR.setText(Simulator.MAR);
			Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
			txtPC.setText(Simulator.PC);
			Simulator.MBR = assembler_obj.hexToBin16(Simulator.memory[assembler_obj.binToDec(Simulator.MAR)]);
			txtMBR.setText(Simulator.MBR);
			Simulator.IR = Simulator.MBR;
			txtIR.setText(Simulator.IR);
			if (!Simulator.IR.equals("0000000000000000")) {
				single_instruction(Simulator.IR);
				setTblMemory();
			} else {
				txtarea_instructions.setText("Halt reached");
				Simulator.PC = Simulator.MAR;
			}
		} catch (Exception ex) {
			txtarea_instructions.setText("Error running instruction");
		}
	}

	private void single_instruction(String instruction) {
		// Function that decodes the instruction and then runs that correspondng
		// instruction
		Assembler assembler_obj = new Assembler();
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
		txtarea_instructions.setText("Executed Instruction:\n" + ins + " " + assembler_obj.binToHex(Reg) + ","
				+ assembler_obj.binToHex(IX) + "," + Indirect + "," + assembler_obj.binToHex(Add));
		String EA, mem, res;
		int mem_loc;

		switch (ins) {
		case "JZ":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
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
				mem = Simulator.memory[Integer.parseInt(res)];
				if (assembler_obj.hexToDec(res) > 4095) {
					txtarea_instructions.setText("Memory limit exceeded");
				}
				else {
					if (assembler_obj.hexToDec(mem) == 0) {
						Simulator.PC =assembler_obj.hexToBin16(EA);
					} else
						Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
				}
			break;
		case "JNE":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			res="";
			if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
				res = R0;
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
				res = R1;
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
				res = R2;
			} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
				res = R3;
			}
			mem = Simulator.memory[Integer.parseInt(res)];
			if (assembler_obj.hexToDec(res) > 4095) {
				txtarea_instructions.setText("Memory limit exceeded");
			}
			else {
				if (assembler_obj.hexToDec(mem) != 0) {
					Simulator.PC = assembler_obj.hexToBin16(EA);
				} else
					Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
			}
			break;
		case "JCC":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			res="";
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
				} else
					Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
			
			break;
		case "JMA":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
				mem = Simulator.memory[assembler_obj.hexToDec(EA)];
				res = assembler_obj.hexToBin16(mem);
				Simulator.PC = assembler_obj.hexToBin16(EA);
			break;
		case "JSR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
				res = assembler_obj.hexToBin16(EA);
				R3 = assembler_obj.addBin(Simulator.PC, "1");
				Simulator.PC = res;
			break;
		case "RFS":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = Simulator.memory[assembler_obj.hexToDec(R3)];
				res = assembler_obj.hexToBin16(mem);
				R0 = assembler_obj.hexToBin16(EA);
				Simulator.PC = res;
			}
			break;
		case "SOB":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			
				mem = Simulator.memory[assembler_obj.hexToDec(EA)];
				res = assembler_obj.hexToBin16(mem);

				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					res=Simulator.memory[assembler_obj.binToDec(R0)];
					R0 = assembler_obj.decToBin(Integer.parseInt(res)-1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					res=Simulator.memory[assembler_obj.binToDec(R0)];
					R1 = assembler_obj.decToBin(Integer.parseInt(res)-1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					res = R2;
					res=Simulator.memory[assembler_obj.hexToDec(res)];
					R2 = assembler_obj.decToBin(Integer.parseInt(res)-1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					res = R3;
					res=Simulator.memory[assembler_obj.hexToDec(res)];
					R3 = assembler_obj.decToBin(Integer.parseInt(res)-1);
				}
				
				if (Integer.parseInt(res) != 0) {
					Simulator.PC = EA;
				} else
					Simulator.PC = assembler_obj.addBin(Simulator.PC, "1");
				if (assembler_obj.hexToDec(EA) > 4095) {
					txtarea_instructions.setText("Memory limit exceeded");
				} else {}
			break;
		case "LDR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (assembler_obj.hexToDec(EA) > 4095) {
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				mem = Simulator.memory[assembler_obj.hexToDec(EA)];
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
			break;
		case "LDA":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
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
			break;
		case "LDX":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (Integer.parseInt(IX) == 0) {
				txtarea_instructions.setText("Index register can't be 0 in this case");
			} else {
				if (assembler_obj.hexToDec(EA) > 4095) {
					txtarea_instructions.setText("Memory limit exceeded");
				} else {
					mem = Simulator.memory[assembler_obj.hexToDec(EA)];
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
			break;
		case "STR":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			mem_loc = assembler_obj.hexToDec(EA);
			if (mem_loc > 4095) {
				txtarea_instructions.setText("Memory limit exceeded");
			} else {
				if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("00")) {
					Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.R0);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("01")) {
					Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.R1);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("10")) {
					Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.R2);
				} else if (Integer.parseInt(Reg) == java.lang.Integer.parseInt("11")) {
					Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.R3);
				}
			}
			break;
		case "STX":
			EA = assembler_obj.EffectiveAddress(instruction.substring(8, 16));
			if (Integer.parseInt(IX) == 0) {
				txtarea_instructions.setText("Index register can't be 0 in this case");
			} else {
				mem_loc = assembler_obj.hexToDec(EA);
				if (mem_loc > 4095) {
					txtarea_instructions.setText("Memory limit exceeded");
				} else {
					if (Integer.parseInt(IX) == java.lang.Integer.parseInt("01")) {
						Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.IX1);
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("10")) {
						Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.IX2);
					} else if (Integer.parseInt(IX) == java.lang.Integer.parseInt("11")) {
						Simulator.memory[mem_loc] = assembler_obj.binToHex(Simulator.IX3);
					}
				}
			}
			break;
		default:
			break;
		}
	}

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
	// Code">//GEN-BEGIN:initComponents
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
		lblF = new javax.swing.JLabel();
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
		lblD = new javax.swing.JLabel();
		lblA = new javax.swing.JLabel();
		lblI = new javax.swing.JLabel();
		lblX = new javax.swing.JLabel();
		lblW = new javax.swing.JLabel();
		txtF = new javax.swing.JTextField();
		txtD = new javax.swing.JTextField();
		txtA = new javax.swing.JTextField();
		txtI = new javax.swing.JTextField();
		txtX = new javax.swing.JTextField();
		txtW = new javax.swing.JTextField();
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
		lblCC = new javax.swing.JLabel();
		txtCC = new javax.swing.JTextField();

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

		lblF.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblF.setText("F");

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

		lblD.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblD.setText("D");

		lblA.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblA.setText("A");

		lblI.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblI.setText("I");

		lblX.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblX.setText("X");

		lblW.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		lblW.setText("W");

		txtF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtF.setText("0");

		txtD.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtD.setText("0");

		txtA.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtA.setText("0");

		txtI.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtI.setText("0");

		txtX.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtX.setText("0");

		txtW.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtW.setText("0");

		txtIXR12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		txtIXR12.setText("00");

		tblMemory.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "Location", "Data" }) {
			Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };
			boolean[] canEdit = new boolean[] { true, false };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
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

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup().addContainerGap()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblPC, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtPC, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_PC))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblIXR3, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18).addComponent(txtIXR3,
																javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblIXR2, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18).addComponent(
																txtIXR2, javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblR2, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtR2, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_R2))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblR1, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtR1, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_R1))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblR4, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtR3, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_R3))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblIXR1, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18).addComponent(txtIXR1,
																javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblR0, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtR0, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_R0))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblMAR, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtMAR, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_MAR))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblMBR, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(txtMBR, javax.swing.GroupLayout.PREFERRED_SIZE,
																160, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(btn_LD_MBR))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblIR, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18).addComponent(txtIR,
																javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(lblMFR,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(20, 20,
																		20)
																.addComponent(txtMFR,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(layout.createSequentialGroup().addComponent(
																txtOpCode, javax.swing.GroupLayout.PREFERRED_SIZE, 72,
																javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(txtGPR,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(txtIXR12,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 30,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(txtIndirect,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 23,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(
																		txtAddress,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 56,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
																.createSequentialGroup().addComponent(lblOpcode)
																.addGap(26, 26, 26).addComponent(lblGPR)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(lblIXR)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(lblImmediate,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 25,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(lblI1)))
												.addGroup(layout.createSequentialGroup()
														.addComponent(lblCC, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18).addComponent(txtCC,
																javax.swing.GroupLayout.PREFERRED_SIZE, 160,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(18, 18, 18)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 327,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup().addGap(17, 17, 17)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																		layout.createSequentialGroup()
																				.addGap(0, 0, Short.MAX_VALUE)
																				.addComponent(txtI,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						27,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(txtX,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						27,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(txtW,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						27,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																.addGroup(layout.createSequentialGroup().addGroup(layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(btn_Run,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGroup(layout.createSequentialGroup()
																				.addGap(8, 8, 8).addComponent(lblI)
																				.addGap(30, 30, 30)
																				.addComponent(lblX,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						11,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addGap(18, 18, 18).addComponent(lblW,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						15,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addComponent(btn_SI,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(btn_IPL,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(btn_Load,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(btn_Store,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGap(0, 8, Short.MAX_VALUE))))
												.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
														.createSequentialGroup().addGap(24, 24, 24)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(txtF,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 27,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblF))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(txtD,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 27,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblD))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(lblA).addComponent(txtA,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 27,
																		javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addContainerGap(18, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(16, 16, 16)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addComponent(jScrollPane2).addGap(18, 18, 18)
										.addComponent(
												jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout.createSequentialGroup()
														.addComponent(btn_IPL, javax.swing.GroupLayout.PREFERRED_SIZE,
																50, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(btn_SI, javax.swing.GroupLayout.PREFERRED_SIZE,
																50, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(btn_Run, javax.swing.GroupLayout.PREFERRED_SIZE,
																50, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(btn_Load, javax.swing.GroupLayout.PREFERRED_SIZE,
																50, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(18, 18, 18)
														.addComponent(btn_Store, javax.swing.GroupLayout.PREFERRED_SIZE,
																50, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblF).addComponent(lblD)
																.addComponent(lblA))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(txtF,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		txtD, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		txtA, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGap(18, 18, 18)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblI).addComponent(lblX)
																.addComponent(lblW))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(txtI,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		txtX, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		txtW, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addGroup(layout.createSequentialGroup().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(lblR0)
														.addComponent(txtR0, javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(btn_LD_R0)).addGap(7, 7, 7)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblR1)
																.addComponent(txtR1,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(btn_LD_R1))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblR2)
																.addComponent(txtR2,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(btn_LD_R2))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblR4)
																.addComponent(txtR3,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(btn_LD_R3))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(txtIXR1,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblIXR1))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblIXR2).addComponent(
																		txtIXR2, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGap(11, 11, 11)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblIXR3).addComponent(
																		txtIXR3, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGap(14, 14, 14)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		txtPC, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblPC).addComponent(btn_LD_PC))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		txtMAR, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblMAR).addComponent(btn_LD_MAR))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		txtMBR, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblMBR).addComponent(btn_LD_MBR))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		txtIR, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblIR))
														.addGap(11, 11, 11)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		txtMFR, javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblMFR))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(lblCC).addComponent(txtCC,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11,
												Short.MAX_VALUE)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(lblI1).addComponent(lblImmediate).addComponent(lblIXR)
												.addComponent(lblGPR).addComponent(lblOpcode))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtOpCode, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtGPR, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtIXR12, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtIndirect, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap()));

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

	private void btn_StoreActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_StoreActionPerformed
		// TODO add your handling code here:
		Assembler assembler_obj = new Assembler();
		Simulator.memory[assembler_obj.binToDec(txtMAR.getText())] = assembler_obj.binToHex(txtMBR.getText());
		setTblMemory();
	}// GEN-LAST:event_btn_StoreActionPerformed

	private void btn_LoadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_LoadActionPerformed
		// TODO add your handling code here:
		Assembler assembler_obj = new Assembler();
		String ins = assembler_obj.hexToBin16(Simulator.memory[assembler_obj.binToDec(txtMAR.getText())]);
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
	private javax.swing.JButton btn_Load;
	private javax.swing.JButton btn_Run;
	private javax.swing.JButton btn_SI;
	private javax.swing.JButton btn_Store;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel lblA;
	private javax.swing.JLabel lblCC;
	private javax.swing.JLabel lblD;
	private javax.swing.JLabel lblF;
	private javax.swing.JLabel lblGPR;
	private javax.swing.JLabel lblI;
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
	private javax.swing.JLabel lblW;
	private javax.swing.JLabel lblX;
	private javax.swing.JTable tblMemory;
	private javax.swing.JTextField txtA;
	private javax.swing.JTextField txtAddress;
	private javax.swing.JTextField txtCC;
	private javax.swing.JTextField txtD;
	private javax.swing.JTextField txtF;
	private javax.swing.JTextField txtGPR;
	private javax.swing.JTextField txtI;
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
	private javax.swing.JTextField txtW;
	private javax.swing.JTextField txtX;
	private javax.swing.JTextArea txtarea_instructions;
	// End of variables declaration//GEN-END:variables
}
