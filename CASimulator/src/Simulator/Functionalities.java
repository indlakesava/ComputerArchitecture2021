/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulator;

/**
 *
 * @author indla
 */
public class Functionalities { 
    //Setting memory after reading the ipl.txt file
    public void set_memory(String ipl_txt)
    {
        //Reads ipl_txt data passed and loads the memoy
        Assembler assembler_obj = new Assembler();
        String[] lines = ipl_txt.split(System.getProperty("line.separator"));
        for(String s: lines){
            String[] splitted = s.split(" ");
            int num = assembler_obj.hexToDec(splitted[0]);
            if (splitted.length == 2) {
                Simulator.memory[num] = splitted[1];
            } else {
                Simulator.memory[num] = assembler_obj.instructionToWord(splitted[1], splitted[2]);
            }
        }
    }
}
