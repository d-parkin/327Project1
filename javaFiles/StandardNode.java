package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

    // nested class to perform the Threading without the need of 3 files
    static class BroadcastListener extends Thread {
        @Override
        public void run() {
            listenToBroadcast();
        }

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
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength()); //building an easy string message
                    System.out.printf("Received the following message: \"%s\" from: %s %n", msg, receivePacket.getAddress());
                }
            } catch (IOException io) {
                System.out.println("Couldn't create socket or had problems receiving a broadcast message: " + io);
            }
        }
    }

    // nested class to perform the Threading without the need of 3 files
    static class MulticastListener extends Thread {

        public void run() {
            listenToMulticast();
        }

        /**
         * The method is being used to listen to multicast messages on 225.1.1.1::5000
         */
        private static void listenToMulticast() {
            int port = 5000; // Defining the port
            byte[] receiveBuffer = new byte[1024];
            try (MulticastSocket socket = new MulticastSocket(port)) {

                InetAddress group = InetAddress.getByName("225.1.1.1"); // All addresses between 224.0.0.0 and 239.255.255.255 are multicast
                socket.joinGroup(group); // joining the group
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                while (true) {
                    socket.receive(receivePacket); // checking packet and writing to the buffer
                    String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.printf("Received the message: %s from %s %n", msg, receivePacket.getAddress());
                    if ("quit".equals(msg)) {
                        break;
                    }
                }
                socket.leaveGroup(group);
            } catch (IOException e) {
                System.out.println("A problem with joining/leaving the group or receiving a multicast packet occurred: " + e);
            }
        }


    }
}