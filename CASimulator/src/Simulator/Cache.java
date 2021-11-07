/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulator;

/**
 *
 * @author indla
 */
public class Cache {
    public void set_memory(int add,String data) {
        Simulator.memory[add] = data;
        Assembler assembler_obj = new Assembler();
        String tag_cache = assembler_obj.decToBin16(add).substring(0, 14);
        //int offset_cache = assembler_obj.binToDec(assembler_obj.decToBin16(add).substring(14));
        
        if(Simulator.cache_d.containsKey(tag_cache)) {
            String[] temp = Simulator.cache_d.get(tag_cache);
            temp[add%4] = data;
            Simulator.cache_d.replace(tag_cache, temp);
        } else {
            if(Simulator.cache_q.size()==16){
                Simulator.cache_d.remove(Simulator.cache_q.peek());
                Simulator.cache_q.remove();
            }
            String[] temp = new String[4];
            int tag = Math.floorDiv(add, 4)*4;
            for(int i=0; i<4; i++){
                temp[i] = Simulator.memory[tag+i];
            }
            Simulator.cache_d.put(tag_cache, temp);
            Simulator.cache_q.add(tag_cache);
        }
    }
    
    public String get_memory(int add){
        Assembler assembler_obj = new Assembler();
        String tag_mem = assembler_obj.decToBin(Math.floorDiv(add, 4));
        String offset_mem = assembler_obj.decToBin(add%4);
        
        String tag_cache = assembler_obj.decToBin16(add).substring(0, 14);
        int offset_cache = assembler_obj.binToDec(assembler_obj.decToBin16(add).substring(14));
        
        if(Simulator.cache_d.containsKey(tag_cache)){
            return Simulator.cache_d.get(tag_cache)[offset_cache];
        }else {
            if(Simulator.cache_q.size()==16){
                Simulator.cache_d.remove(Simulator.cache_q.peek());
                Simulator.cache_q.remove();
            } 
            String[] temp = new String[4];
            int tag = Math.floorDiv(add, 4)*4;
            for(int i=0; i<4; i++){
                temp[i] = Simulator.memory[tag+i];
            }
            Simulator.cache_d.put(tag_cache, temp);
            Simulator.cache_q.add(tag_cache);
            return Simulator.memory[add];
        }
    }
}
