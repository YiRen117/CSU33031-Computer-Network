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
    InetSocketAddress dstAddress;
    String srcName;


    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public void onReceipt(DatagramPacket packet) {
        try {
            System.out.print("[Received packet] ");

            PacketContent content= PacketContent.fromDatagramPacket(packet);

            if (content.getType()==PacketContent.STRINGPACKET) {
                if(content.toString().startsWith("ACK: ")){
                    System.out.println(content.toString());
                } else{
                    System.out.println("File request: " + content.toString());
                    String fname = content.toString();
                    File file;
                    FileInputStream fin;

                    int size;
                    byte[] buffer;
                    DatagramPacket filePacket;

                    file= new File(fname);				// Reserve buffer for length of file and read file
                    buffer= new byte[(int)file.length()];
                    fin= new FileInputStream(file);
                    size= fin.read(buffer);
                    if (size==-1) {
                        fin.close();
                        throw new Exception("Problem with File Access:"+fname);
                    }
                    System.out.println("File size: " + buffer.length);

                    FileInfoContent fcontent= new FileInfoContent(fname, size);
                    System.out.println("[Sending packet w/ name & length]"); // Send packet with file name and length
                    filePacket= fcontent.toDatagramPacket();
                    filePacket.setSocketAddress(dstAddress);
                    socket.send(filePacket);
                    System.out.println("[Packet sent]");
                    fin.close();
                }
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }


    /**
     * Sender Method
     *
     */

    public synchronized void start() throws Exception {
        System.out.println(this.srcName + " waiting for contact");
        this.wait();
    }

}