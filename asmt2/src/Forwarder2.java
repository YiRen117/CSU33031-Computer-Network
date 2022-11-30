import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forwarder2 extends Node {
    static final int DEFAULT_PORT = 54321;
    static final int DEFAULT_CLI_PORT = 50000;
    static final int DEFAULT_DST_PORT = 50005;
    static final String DEFAULT_NODE = "fw2";
    static final String DEFAULT_CLI_NODE = "app2";
    static final String DEFAULT_DST_NODE = "controller";
    Map<String, String> FW_MAP = new HashMap<>();
    String message = "";
    byte originID, destID;
    /*
     *
     */
    Forwarder2(int port) {
        try {
            socket= new DatagramSocket(port);
            listener.go();
        }
        catch(java.lang.Exception e) {e.printStackTrace();}
    }

    /**
     * Assume that incoming packets contain a String and print the string.
     */
    public void onReceipt(DatagramPacket packet) {
        try {
            System.out.print("[Received packet] ");

            PacketContent content= PacketContent.fromDatagramPacket(packet);

            DatagramPacket response;
            switch (content.getPacketType()) {
                case PacketContent.REQUEST:
                    String dest = "";
                    for(String s : ID_MAP.keySet()){
                        if(ID_MAP.get(s) == content.getDestID()){
                            dest = s;
                        }
                    }
                    System.out.println("Incoming packet waiting to be forwarded. Destination ID: " + dest);

                    response= new ACKContent("ACK: OK - Received request").toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    socket.send(response);

                    if(FW_MAP.containsKey(dest)){
                        String nextHop =  FW_MAP.get(dest);
                        System.out.printf("[Sending packet to %s]", nextHop);
                        packet.setSocketAddress(new InetSocketAddress(nextHop, (nextHop.equals(DEFAULT_CLI_NODE) ?
                                DEFAULT_CLI_PORT : DEFAULT_PORT)));
                        sender = new Sender(socket, packet);
                        sender.start();
                    }
                    else{
                        message = content.toString();
                        originID = content.getOriginID();
                        destID = content.getDestID();
                        DatagramPacket consultation = new ConsultContent(content.getOriginID(), content.getDestID(),
                                DEFAULT_NODE + "," + dest).toDatagramPacket();
                        consultation.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_DST_PORT));
                        System.out.println("Unknown destination. Sending consultation to controller.");
                        socket.send(consultation);
                    }
                    break;

                case PacketContent.TABLEMOD:
                    System.out.println("Received initial forwarding table.");

                    response= new ACKContent("ACK: OK - Received table").toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    socket.send(response);

                    ArrayList<String> init = ((ModContent) content).getTable();
                    for(int i = 0; i < init.size(); i++){
                        FW_MAP.put(init.get(i), init.get(i));
                    }
                    System.out.println("Forwarding table generated.");
                    break;

                case PacketContent.UPDATE:
                    System.out.println("Received new data. Updating forwarding table.");

                    response= new ACKContent("ACK: OK - Received data").toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    socket.send(response);

                    String[] newPath = content.toString().split(",");
                    FW_MAP.put(newPath[0], newPath[1]);

                    System.out.println("Sending packet to the next hop: " + newPath[1]);
                    DatagramPacket request = new RequestContent(originID, destID, message).toDatagramPacket();
                    request.setSocketAddress(new InetSocketAddress(newPath[1], DEFAULT_PORT));
                    sender = new Sender(socket, request);
                    sender.start();
                    break;

                case PacketContent.ACK:
                    String ackmsg = content.toString();
                    System.out.println(ackmsg);
                    if(ackmsg.equals("ACK: OK - Received request")) {
                        sender.ackReceipt();
                    }
                    break;

                default:
                    System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
                    break;
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }


    public synchronized void start() throws Exception {
        System.out.println("Sending Hello Message to the controller.");
        DatagramPacket consultation = new HelloContent(DEFAULT_NODE).toDatagramPacket();
        consultation.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_DST_PORT));
        socket.send(consultation);
        System.out.println("Waiting for contact");
        this.wait(90000);
    }

    /*
     *
     */
    public static void main(String[] args) {
        try {
            (new Forwarder2(DEFAULT_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }
}
