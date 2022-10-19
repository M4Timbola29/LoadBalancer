/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Timbola Principe
 */
public class Node {

    private final String name;
    private final int port;
    private final int jobCapacity;
    private DatagramSocket socket;

    public Node(String name, int port, int jobCapacity) {
        this.name = name;
        this.port = port;
        this.jobCapacity = jobCapacity;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter Node name: ");

        String name = userInput.nextLine();
        
        userInput = new Scanner(System.in);
        System.out.println("Enter Node port: ");

        int port = parseInt(userInput.nextLine());
        
        userInput = new Scanner(System.in);
        System.out.println("Enter Node Capacity: ");

        int jobCapacity = parseInt(userInput.nextLine());
        
        Node node = new Node(name, port, jobCapacity);
        
        try{
            InetAddress address = InetAddress.getByName("localhost");
            String message = "REGISTER,127.0.0.1," + node.port + "," + node.name + "," + node.jobCapacity;
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 4000);
            node.socket = new DatagramSocket(port);
            node.socket.send(packet);
            System.out.println(node.name + " is running...");
            
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                node.socket.receive(inPacket);

                String inMessage = new String(buffer);

                String[] elements = inMessage.trim().split(",");

                switch (elements[0]) {
                    case "JOB":
                        int numberOfJobs = parseInt(elements[1]);
                        int jobDuration = parseInt(elements[2]);
                        node.startJobs(numberOfJobs, jobDuration);
                        break;

                    case "STOP":
                        System.out.println("Stopping...");
                        node.socket.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Don't understand " + message);
                }
            }
            
        } catch(IOException e){
            System.out.println("Node Error: " + e);
        }
    }
    
    public void startJobs(int numberOfJobs, int jobDuration) {
        System.out.println("Doing " + numberOfJobs + " on " + this.name);
        List<Job> jobs = new ArrayList<>();
        for(int i = 0; i < this.jobCapacity; i++) {
            Job job = new Job(jobDuration);
            jobs.add(job);
            job.start();
        }
        Thread t = new Thread(() -> {
            boolean done = false;
            while(!done) {
                done = true;
                for(Job job : jobs) {
                    if(job.running) {
                        done = false;
                    }
                }
            }
            try {
                jobsCompleted(numberOfJobs);
            } catch (IOException ex) {
                System.out.println("Node Error: " + ex);
            }
        });
        t.start();
    }
    
    public void jobsCompleted(int numberOfJobs) throws IOException {
        System.out.println("Jobs Completed");
        String message = "DONE," + numberOfJobs + "," + this.name;
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 4000);
        this.socket.send(packet);
    }
}

class Job extends Thread {

    private final int jobDuration;
    public boolean running = false;
    
    public Job(int jobDuration) {
        this.jobDuration = jobDuration;
    }
    
    @Override
    public void run() {
        running = true;
        try {
            Thread.sleep(jobDuration * 1000);
        } catch (InterruptedException ex) {
            System.out.println("Node Error: " + ex);
        }
        running = false;
    }
    
}
