package javaFiles;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;


/**
 * A special node in the network sending broadcast messages in and interval of 5 seconds and multicast messages in an interval of 7 seconds.
 */
public class MasterNode {

    private static Timestamp broadcastStart;
    private static Timestamp multicastStart;
    public static void main(String[] args) {

        // Starting to separate threads to run the broadcasting and multicasting parallel
        Thread broadcast = new Broadcast();
        Thread multicast = new Multicast();
        broadcast.start();
        multicast.start();
    }

    //New Thread to wait for incoming messages and calculate the time that the broadcast and multicast take
    public void CalculateProceedingTime(){

    }

    // nested class to perform the Threading without the need of 3 files
    static class Broadcast extends Thread {

        @Override
        public void run() {
            broadcast("Hello (Broadcast)");
        }



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
                    multicastStart = new Timestamp(System.currentTimeMillis());
                    socket.send(packet); //sending the packet to the broadcast addresses
                    Thread.sleep(5000L); // waiting 5 seconds after every broadcast
                }
            } catch (IOException  e) {
                System.out.println("Problems with the broadcast method and : " + e);
            } catch (InterruptedException e){
                System.out.println("The broadcast thread was interrupted during sleep(): " + e);
            }
        }
    }

    // nested class to perform the Threading without the need of 3 files
    static class Multicast extends Thread {
        @Override
        public void run() {
            multicast("Are y'all awake? (Multicast)");
        }

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
                    broadcastStart = new Timestamp(System.currentTimeMillis()); // getting the timestamp of the start
                    socket.send(packet); // sending the packet to members of the group
                    Thread.sleep(7000L); // waiting 7 seconds after every broadcast
                }
            } catch (IOException io) {
                System.out.println("An io error occurred in the multicast thread: " + io);
            } catch (InterruptedException e) {
                System.out.println("The multicast thread was interrupted during sleep(): " + e);
            }
        }
    }
}
