import java.io.FileNotFoundException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Worker extends Node {
    static final int DEFAULT_DST_PORT = 50001;
    static final String DEFAULT_DST_NODE = "server";
    InetSocketAddress dstAddress;
    String srcName;


    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public void onReceipt(DatagramPacket packet) {
        System.out.print("[Received packet] ");

        PacketContent content= PacketContent.fromDatagramPacket(packet);
        switch (content.getPacketType()) {
            case PacketContent.ACKPACKET:
                System.out.println(content.toString());
                sender.ackReceipt();
                break;
            case PacketContent.FILEREQUEST:
                fileLookUp(content.toString());
                break;
            default:
                System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
                break;
        }
    }

    public void fileLookUp(String fname){

        DatagramPacket response = null;
        try {
            System.out.println("File request: " + fname);
            File file;
            FileInputStream fin;

            int size;
            byte[] buffer;

            file = new File(fname);                // Reserve buffer for length of file and read file
            buffer = new byte[(int) file.length()];
            fin = new FileInputStream(file);
            size = fin.read(buffer);
            if (size == -1) {
                fin.close();
                throw new Exception("Problem with File Access:" + fname);
            }
            System.out.println("File size: " + buffer.length);

            FileInfoContent fcontent = new FileInfoContent(fname, size);
            System.out.println("[Sent packet w/ name & length]"); // Send packet with file name and length
            response = fcontent.toDatagramPacket();
            response.setSocketAddress(dstAddress);
            sender = new Sender(socket, response);
            sender.start();
            fin.close();
        } catch (FileNotFoundException e) {
            System.out.println(fname + " is not found.");

            try{
                StringPacketContent scontent = new StringPacketContent("Error: File '" + fname + "' not found");
                System.out.println("[Sent packet w/ error message]");
                response = scontent.toDatagramPacket();
                response.setSocketAddress(dstAddress);
                sender = new Sender(socket, response);
                sender.start();
            } catch(Exception e1) {}

        } catch(Exception e) {}
    }

    /**
     * Sender Method
     *
     */

    public synchronized void start() throws Exception {
        System.out.println(this.srcName + " waiting for contact");
        this.wait(90000);
    }

}