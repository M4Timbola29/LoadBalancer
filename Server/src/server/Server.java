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
public class Server {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Server
        int serverPort = 4000;
        ServerSystem server = new ServerSystem(serverPort);
        server.runSystem();
    }
    
}
