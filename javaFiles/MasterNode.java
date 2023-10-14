package javaFiles;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * A special node in the network sending broadcast messages in and interval of 5 seconds and multicast messages in an interval of 7 seconds.
 */
public class MasterNode {

    private static long broadcastStart;
    private static long multicastStart;

    public static void main(String[] args) {

        // Starting to separate threads to run the broadcasting and multicasting parallel
        Thread broadcast = new Broadcast();
        Thread multicast = new Multicast();
        Thread broadcastLog = new BroadcastLog();
        Thread multicastLog = new MulticastLog();
        broadcast.start();
        broadcastLog.start();
        multicast.start();
        multicastLog.start();
    }

    // nested class to perform the Threading without the need of 3 files
    static class Broadcast extends Thread {

        /**
         * The broadcasting method that's sending the messages into the network every 5 seconds
         *
         * @param broadcastMessage The string to be broadcast
         */
        private static void broadcast(String broadcastMessage) {
            // try with resource to make sure the socket will always be closed
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true); // setting the broadcast flag to ensure the programm has the rights to perform a broadcast depending on the OS
                InetAddress address = InetAddress.getByName("255.255.255.255"); // using the broadcast address for a local network
                byte[] buffer = broadcastMessage.getBytes(); // converting the String to a byte buffer to be able to send it in a datagram

                DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length, address, 3000); //preparing the datagram package
                for (int i = 0; i < 20; i++) {
                    broadcastStart = System.nanoTime(); // for calculating the time later
                    socket.send(packet); //sending the packet to the broadcast addresses
                    Thread.sleep(5000L); // waiting until the broadcast before has been calculated
                }
            } catch (IOException  e) {
                System.out.println("Problems with the broadcast method and : " + e);
            } catch (InterruptedException e){
                System.out.println("The broadcast thread was interrupted during sleep(): " + e);
            }
        }

        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            broadcast("Hello (Broadcast)");
        }
    }

    // nested class to perform the Threading without the need of 3 files
    static class Multicast extends Thread {

        /**
         * The multicast method sending messages to a specific group every 7 seconds
         *
         * @param multicastMessage The message to be sent to members of the group
         */
        private static void multicast(String multicastMessage) {
            // try with resource to make sure the socket will always be closed

            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress group = InetAddress.getByName("225.1.1.1"); // Choosing an address from the multicast range
                byte[] buf = multicastMessage.getBytes(); // Converting the message to bytes

                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 5000); // Specifying the packet
                for (int i = 0; i < 20; i++) {
                    multicastStart = System.nanoTime(); // getting the timestamp of the start
                    //System.out.println("MulticastStart: " + multicastStart);
                    socket.send(packet); // sending the packet to members of the group
                    Thread.sleep(7000L); // waiting 7 seconds after every broadcast
                }
            } catch (IOException io) {
                System.out.println("An io error occurred in the multicast thread: " + io);
            } catch (InterruptedException e) {
                System.out.println("The multicast thread was interrupted during sleep(): " + e);
            }
        }
        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            multicast("Are y'all awake? (Multicast)");
        }
    }

    //  LOGGING, New Thread to wait for incoming broadcast responses and log relevant info
    static class BroadcastLog extends Thread{

        /**
         * * LOGGING
         * Listens to response messages from broadcast and extracts important details
         */
        public void CalculateTimeBroadcast(){
            int port = 3333;
            byte[] receiveDataBuffer = new byte[1024]; // expecting messages of max 1024 byte size
            List<Long> timestamps = new ArrayList<>() ; // storing the 4 incoming responses to see which one is the latest
            long latestTS;
            try (DatagramSocket receiveReturnMessages = new DatagramSocket(port) ){
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveDataBuffer, receiveDataBuffer.length);// creating the datagram including the buffer to which the received data will be written

                    receiveReturnMessages.receive(receivePacket); //blocks until a packet is received
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength()); //building an easy string message
                    String[] infos = msg.split(",");
                    //System.out.printf("Received the following message: \"%s\" from: %s %n", msg, receivePacket.getAddress());
                    if (timestamps.size()<3) { // because we have 4 nodes next to the master
                        timestamps.add(Long.valueOf(infos[0]));
                        //System.out.println("received an answer from: " + receivePacket.getAddress() + "::" + receivePacket.getPort());
                    } else {
                    timestamps.add(Long.valueOf(infos[0]));
                        latestTS = timestamps.get(0);
                        for (Long t: timestamps){
                            if (latestTS- t < 0L){
                                latestTS = t;
                            }
                        }

                    long spendTime = Long.valueOf(infos[0])-broadcastStart;
                    System.out.printf("Broadcast took: %d ns, was send from %s::%s, send to 255.255.255.255::3000 and used UDP %n", spendTime, infos[1],infos[2]);
                    timestamps.clear();
                    //}
                }

            } catch (IOException e){
                System.out.println("An problem with the socket or receiving the messages during broadcast log occurred: " + e);
            }
        }

        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            CalculateTimeBroadcast();
        }
    }

    //LOGGING, New Thread to wait for incoming broadcast responses and log relevant info
    static class MulticastLog extends Thread{

        /**
         * LOGGING
         * Listens to response messages from multicast and extracts important details
         */
        public void CalculateTimeMulticast(){
            int port = 5555;
            byte[] receiveDataBuffer = new byte[1024]; // expecting messages of max 1024 byte size
            List<Long> timestamps = new ArrayList<>() ; // storing the 4 incoming responses to see which one took the longest
            long latestTS;
            try (DatagramSocket receiveReturnMessages = new DatagramSocket(port) ){
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveDataBuffer, receiveDataBuffer.length);// creating the datagram including the buffer to which the received data will be written

                    receiveReturnMessages.receive(receivePacket); //blocks until a packet is received
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength()); //building an easy string message
                    String[] infos = msg.split(",");
                    //System.out.printf("Received the following message: \"%s\" from: %s %n", msg, receivePacket.getAddress());
                    if (timestamps.size()<3) { // because we have 4 nodes next to the master
                        timestamps.add(Long.valueOf(infos[0]));
                        //System.out.println("received an answer from: " + receivePacket.getAddress() + "::" + receivePacket.getPort());
                    } else {
                    timestamps.add(Long.valueOf(infos[0]));
                        latestTS = timestamps.get(0);
                        for (Long t: timestamps){
                            if (latestTS- t < 0L){
                                latestTS = t;
                            }
                        }

                    long spendTime = Long.valueOf(infos[0])-multicastStart;
                    System.out.printf("Multicast took: %d ns, was send from %s::%s, send to 225.1.1.1::5000 and used UDP %n", spendTime, infos[1],infos[2]);
                    timestamps.clear();
                    //}
                }

            } catch (IOException e){
                System.out.println("An problem with the socket or receiving the messages during multicast log occurred: " + e);
            }
        }

        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            CalculateTimeMulticast();
        }
    }


}
