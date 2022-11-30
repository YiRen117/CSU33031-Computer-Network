import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
public class Server extends Node {
    static final int DEFAULT_SRC_PORT = 50002;
    static final String DEFAULT_SRC_NODE = "dserver";
    static final int DEFAULT_DST_PORT = 54321;
    static final String DEFAULT_DST_NODE = "cp";
    InetSocketAddress dstAddress;
    String srcName;

    Server(String dstHost, int dstPort, int srcPort, String sourceName) {
        try {
            dstAddress= new InetSocketAddress(dstHost, dstPort);
            socket= new DatagramSocket(srcPort);
            listener.go();
            srcName = sourceName;
        }
        catch(java.lang.Exception e) {e.printStackTrace();
        }
    }

    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public void onReceipt(DatagramPacket packet) {
        try{
            System.out.print("[Received packet] ");

            PacketContent content= PacketContent.fromDatagramPacket(packet);

            DatagramPacket response;
            switch (content.getPacketType()) {
                case PacketContent.REQUEST:
                    response= new ACKContent("ACK: OK - Received message").toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    socket.send(response);

                    String oriID = "";
                    for(String s : ID_MAP.keySet()){
                        if(content.getOriginID() == ID_MAP.get(s)){
                            oriID = s;
                            break;
                        }
                    }
                    System.out.println("Message from " + oriID + " incoming. The content is as below: ");
                    System.out.println(content.toString());
                    break;

                default:
                    System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
                    break;
            }
        } catch(Exception e) {e.printStackTrace();}
    }

    public synchronized void start() throws Exception {
        System.out.println(this.srcName + " waiting for contact");
        this.wait(90000);
    }

    /**
     * Test method
     *
     * Sends a packet to a given address
     */
    public static void main(String[] args) {
        try {
            (new Server(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT, DEFAULT_SRC_NODE)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }

}