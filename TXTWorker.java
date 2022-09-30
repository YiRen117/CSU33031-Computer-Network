import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class TXTWorker extends Worker{
    static final int DEFAULT_DST_PORT = 50001;
    static final int DEFAULT_SRC_PORT = 50002;
    static final String DEFAULT_DST_NODE = "server";
    static final String DEFAULT_SRC_NODE = "TXTWorker";


    TXTWorker(String dstHost, int dstPort, int srcPort, String sourceName) {
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
            (new TXTWorker(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT, DEFAULT_SRC_NODE)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}

