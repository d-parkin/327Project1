package javaFiles;

import java.io.IOException;
import java.net.*;

/**
 * A Standard node in the net waiting to receive messages
 */
public class StandardNode {
    public static void main(String[] args) {
        Thread broadcastListener = new BroadcastListener();
        Thread multicastListener = new MulticastListener();
        broadcastListener.start();
        multicastListener.start();

    }



    // nested class to perform the Threading without the need of multiple files
    static class BroadcastListener extends Thread {
        /**
         * The method is listening to all incoming messages that are broadcast or directly send to the ip on port 3000
         */
        private static void listenToBroadcast() {
            int port = 3000; // Defining the port

            //using try with resources to make sure the socket will be closed after not being needed/used anymore
            try (DatagramSocket receiverSocket = new DatagramSocket(port)) {
                byte[] receiveDataBuffer = new byte[1024]; // expecting messages of max 1024 byte size

                System.out.println("Receiver is listening to incoming messages");

                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveDataBuffer, receiveDataBuffer.length);// creating the datagram including the buffer to which the received data will be written

                    receiverSocket.receive(receivePacket); //blocks until a packet is received
                    Long receivedBroadcastTime = System.nanoTime();
                    // Starting another thread to send messages at the same time as receiving messages on the socket
                    new BroadcastResponse(receiverSocket,receivePacket,receivedBroadcastTime.toString()).start(); // start a different thread to send over the same socket whilst not blocking the receiving
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength()); //building an easy string message
                    System.out.printf("Received the following message: \"%s\" from: %s %n", msg, receivePacket.getAddress());
                }
            } catch (IOException io) {
                System.out.println("Couldn't create socket or had problems receiving a broadcast message: " + io);
            }
        }

        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            listenToBroadcast();
        }
    }



    // LOGGING, nested class to perform the Threading without the need of multiple files
    static class BroadcastResponse extends Thread {

        private DatagramSocket senderSocket; // to reuse the socket on which the broadcast was received
        private String receivedBroadcastTime; // the timestamp of receiving
        private DatagramPacket packet; // the received packet

        public BroadcastResponse(DatagramSocket s,DatagramPacket param, String ts) {
            this.senderSocket=s;
            this.packet = param;
            this.receivedBroadcastTime = ts;
        }

        /**
         * Sending the response to a broadcast, including the time the broadcast message was received  as well as the original senders address and port
         */
        private void sendResponse() {

            try  {
                InetAddress address = packet.getAddress();  // Get the senders address to answer
                String broadcastInfos = receivedBroadcastTime+ "," + address + "," + packet.getPort(); // build the response message string
                byte[] buffer = broadcastInfos.getBytes(); // converting the String to a byte buffer to be able to send it in a datagram


                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 3333); //preparing the datagram package
                System.out.println("Sending to "+ address + "3333");
                senderSocket.send(packet); //sending the packet to the logging port of the sender of the broadcast

            } catch (IOException e) {
                System.out.println("Problems with the broadcast method and : " + e);
            }

        }

        // The method that has to be implemented when you want to run something as a thread
        @Override
        public void run() {
            sendResponse();
        }
    }



    // nested class to perform the Threading without the need of multiple files
    static class MulticastListener extends Thread {

        /**
         * The method is being used to listen to multicast messages on 225.1.1.1::5000
         */
        private static void listenToMulticast() {
            int port = 5000; // Defining the port
            byte[] receiveBuffer = new byte[1024];
            try (MulticastSocket receiverSocket = new MulticastSocket(port)) {

                InetAddress group = InetAddress.getByName("225.1.1.1"); // All addresses between 224.0.0.0 and 239.255.255.255 are multicast
                receiverSocket.joinGroup(group); // joining the group
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                while (true) {
                    receiverSocket.receive(receivePacket); // checking packet and writing to the buffer
                    Long receivedBroadcastTime = System.nanoTime();
                    new MulticastResponse(receiverSocket,receivePacket,receivedBroadcastTime.toString()).start(); // start a different thread to send over the same socket whilst not blocking the receiving
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength()); // getting the data from the packet as a String
                    System.out.printf("Received the message: %s from %s %n", msg, receivePacket.getAddress());
                    if ("quit".equals(msg)) {
                        break;
                    }
                }
                receiverSocket.leaveGroup(group);
            } catch (IOException e) {
                System.out.println("A problem with joining/leaving the group or receiving a multicast packet occurred: " + e);
            }
        }

        // The method that has to be implemented when you want to run something as a thread
        public void run() {
            listenToMulticast();
        }



        // LOGGING,  nested class to perform the Threading without the need of multiple files
        static class MulticastResponse extends Thread {

            private MulticastSocket senderSocket; // to reuse the socket on which the multicast was received
            private String receivedBroadcastTime; // the timestamp of receiving
            private DatagramPacket packet; // the received packet

            public MulticastResponse(MulticastSocket s,DatagramPacket param, String ts) {
                this.senderSocket=s;
                this.packet = param;
                this.receivedBroadcastTime = ts;
            }

            /**
             * Sending the response to a multicast, including the time the broadcast message was received as well as the original senders address and port
             */
            private void sendResponse() {

                try  {
                    InetAddress address = packet.getAddress();  // Get the senders address to answer
                    String broadcastInfos = receivedBroadcastTime+ "," + address + "," + packet.getPort(); // build the response message string
                    byte[] buffer = broadcastInfos.getBytes(); // converting the String to a byte buffer to be able to send it in a datagram


                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 5555); //preparing the datagram package
                    System.out.println("Sending to "+ address + "5555");
                    senderSocket.send(packet); //sending the packet to the logging port of sender of the multicast

                } catch (IOException e) {
                    System.out.println("Problems with the multicast method and : " + e);
                }

            }

            // The method that has to be implemented when you want to run something as a thread
            @Override
            public void run() {
                sendResponse();
            }
        }


    }
}