/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TimbolaPrincipe
 */
public class ServerSystem {

    private final DataManager manager;
    private final int serverPort;
    private DatagramSocket socket;
    private int totalJobs;

    public ServerSystem(int port) {
        manager = new DataManager();
        serverPort = port;
        totalJobs = 0;
    }

    public void runSystem() {
        System.out.println("Server is running...");

        try {
            this.socket = new DatagramSocket(serverPort);
            this.socket.setSoTimeout(0);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(packet);

                String message = new String(buffer);

                String[] elements = message.trim().split(",");

                switch (elements[0]) {
                    case "REGISTER":
                        String IPAddress = elements[1];
                        int port = Integer.parseInt(elements[2].trim());
                        String name = elements[3];
                        int jobCapacity = parseInt(elements[4]);
                        Data newData = new Data(IPAddress, port, name, jobCapacity);
                        manager.registerData(newData);
                        break;

                    case "STOP":
                        this.stopSystem();
                        break;

                    case "JOB":
                        int numberOfJobs = parseInt(elements[1]);
                        int jobDuration = parseInt(elements[2]);
                        Thread t = new Thread(new JobsHandler(this.socket, this.manager, numberOfJobs, jobDuration));
                        t.start();
                        break;

                    case "DONE":
                        this.totalJobs += parseInt(elements[1]);
                        Data nodeData = manager.findData(elements[2]);
                        nodeData.setJobCapacity(nodeData.getJobCapacity() + parseInt(elements[1]));
                        System.out.println(elements[1] + " job(s) done by " + elements[2]);
                        System.out.println("Jobs count: " + this.totalJobs);
                        break;

                    default:
                        System.out.println("Don't understand " + message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSystem() {
        System.out.println("Stopping...");
        for(Object nodeObject : manager.availableData) {
            Data nodeData = (Data) nodeObject;

            String[] nodeStringAddress = nodeData.getIPAddress().split("\\.");

            int nodePort = nodeData.getPort();
            byte[] bytesAddress = new byte[]{
                (byte) parseInt(nodeStringAddress[0]),
                (byte) parseInt(nodeStringAddress[1]),
                (byte) parseInt(nodeStringAddress[2]),
                (byte) parseInt(nodeStringAddress[3])};

            try {
                InetAddress address = InetAddress.getByAddress(bytesAddress);
                String message = "STOP";
                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, nodePort);
                this.socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.socket.close();
        System.exit(0);
    }
}

class JobsHandler implements Runnable {

    private DatagramSocket serverSocket;
    private DataManager manager;
    private int numberOfJobs;
    private int jobDuration;
    
    public JobsHandler(DatagramSocket serverSocket, DataManager manager, int numberOfJobs, int jobDuration) {
        this.serverSocket = serverSocket;
        this.manager = manager;
        this.numberOfJobs = numberOfJobs;
        this.jobDuration = jobDuration;
    }
    
    public Data getTopNode(int numberOfJobs, int jobDuration) {
        Data topNode = null;

        String higherJobsNode = "";
        int higherJobs = 0;
        int totalAvailableJobs = 0;

        for (Object nodeObject : manager.availableData) {
            Data nodeData = (Data) nodeObject;
            totalAvailableJobs += nodeData.getJobCapacity();
            if (nodeData.getJobCapacity() > higherJobs) {
                higherJobs = nodeData.getJobCapacity();
                higherJobsNode = nodeData.getName();
            }
            if (totalAvailableJobs > 0) {
                topNode = manager.findData(higherJobsNode);
            }
        }
        return topNode;
    }
    
    @Override
    public void run() {
        while (numberOfJobs > 0) {
            Data topNode = getTopNode(numberOfJobs, jobDuration);
            if (topNode != null) {
                int sendJobs = topNode.getJobCapacity();
                if (topNode.getJobCapacity() > numberOfJobs) {
                    sendJobs = numberOfJobs;
                }
                String job = "JOB," + sendJobs + "," + jobDuration;

                int nodePort = topNode.getPort();

                String[] addressString = topNode.getIPAddress().split("\\.");
                byte[] addressBytes = new byte[]{
                    (byte) parseInt(addressString[0]),
                    (byte) parseInt(addressString[1]),
                    (byte) parseInt(addressString[2]),
                    (byte) parseInt(addressString[3])};

                try {
                    InetAddress address = InetAddress.getByAddress(addressBytes);
                    DatagramPacket jobPacket = new DatagramPacket(job.getBytes(), job.getBytes().length, address, nodePort);
                    serverSocket.send(jobPacket);
                    System.out.println("Sending " + numberOfJobs + " job(s) to " + topNode.getName() + "...");
                } catch (IOException ex) {
                    Logger.getLogger(JobsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                topNode.setJobCapacity(topNode.getJobCapacity() - sendJobs);

                numberOfJobs -= sendJobs;
            } else {
                System.out.println("Waiting for nodes to be available!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
