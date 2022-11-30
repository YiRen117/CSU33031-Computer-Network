import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller extends Node {
    static final int DEFAULT_PORT = 50005;
    Map<String, Integer> labels = new HashMap<>();
    private static final int[][] TABLE = new int[][] {
            { -1, 1, -1, -1, -1, -1, -1 },
            { 0, -1, -1, 3, -1, -1, 6 },
            { -1, -1, -1, 3, -1, -1, -1 },
            { -1, 1, 2, -1, -1, -1, 6 },
            { -1, -1, -1, -1, -1, 5, -1 },
            { -1, -1, -1, -1, 4, -1, 6 },
            { -1, 1, -1, 3, -1, 5, -1 } };
    private static final int INIT_DIST = 1000;
    private static final int[][] DIST = new int[][] {
            { 0, 1, INIT_DIST, INIT_DIST, INIT_DIST, INIT_DIST, INIT_DIST },
            { 1, 0, INIT_DIST, 1, INIT_DIST, INIT_DIST, 1 },
            { INIT_DIST, INIT_DIST, 0, 1, INIT_DIST, INIT_DIST, INIT_DIST },
            { INIT_DIST, 1, 1, 0, INIT_DIST, INIT_DIST, 1 },
            { INIT_DIST, INIT_DIST, INIT_DIST, INIT_DIST, 0, 1, INIT_DIST },
            { INIT_DIST, INIT_DIST, INIT_DIST, INIT_DIST, 1, 0, 1 },
            { INIT_DIST, 1, INIT_DIST, 1, INIT_DIST, 1, 0 }};


    Controller(int port) {
        try {
            socket= new DatagramSocket(port);
            listener.go();
            this.labels.put("app1", 0);
            this.labels.put("fw1", 1);
            this.labels.put("app2", 2);
            this.labels.put("fw2", 3);
            this.labels.put("dserver", 4);
            this.labels.put("cp", 5);
            this.labels.put("isp", 6);
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
                case PacketContent.ACK:
                    System.out.println(content.toString());
                    sender.ackReceipt();
                    break;

                case PacketContent.CONSULT:
                    String[] incomingString = content.toString().split(",");
                    String startID = incomingString[0];
                    String destID = incomingString[1];
                    System.out.printf("Received consultation with start ID: %s and destination ID: %s.\n", startID, destID);
                    response= new ACKContent("ACK: OK - Received consultation").toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    socket.send(response);

                    String nextHop = destID + "," + searchShortest(startID, destID);
                    response = new UpdateContent(content.getOriginID(), content.getDestID(), nextHop).toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    sender = new Sender(socket, response);
                    sender.start();
                    break;

                case PacketContent.HELLO:
                    String forwarder = content.toString();
                    System.out.println("Received Hello Message from: " + forwarder);
                    String initTable = "";
                    int index = labels.get(forwarder);
                    for(int i = 0; i < TABLE[index].length; i++){
                        if(TABLE[index][i] != -1){
                            String id = "";
                            for(String s : labels.keySet()){
                                if(labels.get(s) == TABLE[index][i]){
                                    id = s;
                                }
                            }
                            initTable = initTable + id + ",";
                        }
                    }
                    System.out.println("Sending initial forwarding table to: " + forwarder);
                    response = new ModContent(initTable).toDatagramPacket();
                    response.setSocketAddress(packet.getSocketAddress());
                    sender = new Sender(socket, response);
                    sender.start();
                    break;

                default:
                    System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
                    break;
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }


    private void dijkstraAlgorithm(int des) {
        boolean[] shortest = new boolean[TABLE.length];
        shortest[des] = true;
        boolean exit = false;
        while(!exit) {
            int vertex = -1;
            for(int i = 0; i < DIST.length; i++) {
                if((!shortest[i]) && (DIST[i][des] != Integer.MAX_VALUE)){
                    vertex = i;
                    shortest[vertex] = true;
                    for (int j = 0; j < DIST.length; j++) {
                        if (DIST[j][vertex] + DIST[vertex][des] < DIST[j][des]) {
                            DIST[j][des] = DIST[j][vertex] + DIST[vertex][des];
                            shortest[j] = false;
                            TABLE[j][des] = vertex;
                        }
                    }
                }
            }
            if(vertex == -1) {
                exit = true;
            }
        }
    }


    public String searchShortest(String start, String end){
        int startIndex = labels.get(start);
        int endIndex = labels.get(end);
        dijkstraAlgorithm(endIndex);
        if(TABLE[startIndex][endIndex] == -1){
            return "Error: No path to the destination node.";
        }
        int nextIndex = TABLE[startIndex][endIndex];
        String nextHop = "";
        for(String s : labels.keySet()){
            if(labels.get(s) == nextIndex){
                nextHop = s;
            }
        }
        return nextHop;
    }

    public synchronized void start() throws Exception {
        System.out.println("Waiting for contact");
        this.wait(90000);
    }

    /*
     *
     */
    public static void main(String[] args) {
        try {
            (new Controller(DEFAULT_PORT)).start();
            System.out.println("Program completed");
        } catch(java.lang.Exception e) {e.printStackTrace();}
    }


}