import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Sender extends Thread{
    private final DatagramPacket packet;
    private final DatagramSocket socket;
    private boolean sent;

    Sender(DatagramSocket skt, DatagramPacket pkt){
        socket = skt;
        packet = pkt;
        sent = false;
    }

    @Override
    public synchronized void run(){
        while(true) {
            try {
                socket.send(packet);
                this.wait(2000);
                if(sent){
                    return;
                }else{
                    System.out.println("Packet sending failed. Will try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void ackReceipt(){
        sent = true;
        this.notify();
    }
    
}
