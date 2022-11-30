import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
    static final int PACKETSIZE = 65536;

    DatagramSocket socket;
    Listener listener;
    CountDownLatch latch;
    Sender sender;
    Map<String, Byte> ID_MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);;

    Node() {
        ID_MAP.put("controller", PacketContent.CONTROLLER);
        ID_MAP.put("app1", PacketContent.APP1);
        ID_MAP.put("fw1", PacketContent.FW1);
        ID_MAP.put("app2", PacketContent.APP2);
        ID_MAP.put("fw2", PacketContent.FW2);
        ID_MAP.put("dserver", PacketContent.SERVER);
        ID_MAP.put("cp", PacketContent.CP);
        ID_MAP.put("isp", PacketContent.ISP);
        latch= new CountDownLatch(1);
        listener= new Listener();
        listener.setDaemon(true);
        listener.start();
    }


    public abstract void onReceipt(DatagramPacket packet);

    /**
     *
     * Listener thread
     *
     * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
     */
    class Listener extends Thread {

        /*
         *  Telling the listener that the socket has been initialized
         */
        public void go() {
            latch.countDown();
        }

        /*
         * Listen for incoming packets and inform receivers
         */
        public void run() {
            try {
                latch.await();
                // Endless loop: attempt to receive packet, notify receivers, etc
                while(true) {
                    DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
                    socket.receive(packet);

                    onReceipt(packet);
                }
            } catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
        }
    }
}
