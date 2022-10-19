/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author TimbolaPrincipe
 */
public class Data {
    private String IPAddress;
    private int port;
    private String name;
    private int jobCapacity;
    
    public Data(String IPAddress, int port, String name, int jobCapacity){
        this.IPAddress = IPAddress;
        this.port = port;
        this.name = name;
        this.jobCapacity = jobCapacity;
    }
    
    public void setIPAddress(String IPAddress){
        this.IPAddress = IPAddress;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPort(int port){
        this.port = port;
    }
    public void setJobCapacity(int jobCapacity){
        this.jobCapacity = jobCapacity;
    }
    public String getIPAddress(){
        return IPAddress;
    }
    public String getName(){
        return name;
    }
    public int getPort(){
        return port;
    }
    public int getJobCapacity(){
        return jobCapacity;
    }
}
