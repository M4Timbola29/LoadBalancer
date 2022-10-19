/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author TimbolaPrincipe
 */
public class Sender {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            InetAddress address = InetAddress.getByName("localhost");
            //String message = "STOP";
            String message = "JOB,1,1";
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 4000);
            DatagramSocket socket = new DatagramSocket(10000);
            socket.send(packet);
            socket.close();
            System.out.println("Sent packet with message: " + message);
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
