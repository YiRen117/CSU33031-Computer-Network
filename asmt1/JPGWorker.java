import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class JPGWorker extends Worker{
    static final int DEFAULT_SRC_PORT = 50004;
    static final String DEFAULT_SRC_NODE = "JPGWorker";


    JPGWorker(String dstHost, int dstPort, int srcPort, String sourceName) {
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
     * Test method
     *
     * Sends a packet to a given address
     */
    public static void main(String[] args) {
        try {
            (new JPGWorker(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT, DEFAULT_SRC_NODE)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}


