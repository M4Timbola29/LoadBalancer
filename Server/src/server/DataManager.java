/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.LinkedList;
/**
 *
 * @author TimbolaPrincipe
 */
public class DataManager {
    
    public LinkedList availableData;
    
    public DataManager(){
        availableData = new LinkedList();
    }
    
    public void registerData(Data data){
        if(data != null){
            System.out.println("Adding data called " + data.getName());
            availableData.add(data);
            
            //Data testData = (Data) availableData.get(0);
            //System.out.println(testData.getName());
        }
    }
    public Data findData(String name){
        Data data = null;
        
        for(int items = 0; items < availableData.size(); items++){
            data = (Data) availableData.get(items);
            if(data != null){
                if(data.getName().equals(name)){
                    return data;
                }
            }
        }
        return data;
    }
    
}
