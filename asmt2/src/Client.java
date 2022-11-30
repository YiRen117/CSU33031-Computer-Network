/**
 *
 */
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Client extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 54321;
	byte src_id;
	InetSocketAddress dstAddress;
	String hostname = "";
	String gateway = "";

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Client(String dstHost, String srcHost, int dstPort, int srcPort) {
		try {
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			this.hostname = srcHost;
			this.gateway = dstHost;
			listener.go();
			src_id = srcHost.equals("app1") ? PacketContent.APP1 : PacketContent.APP2;
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}


	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			System.out.print("[Received packet] ");

			PacketContent content = PacketContent.fromDatagramPacket(packet);

			DatagramPacket response;
			switch (content.getPacketType()){
				case PacketContent.REQUEST:
					System.out.println("Message incoming. Content as below:");
					System.out.println(content.toString());

					response = new ACKContent("ACK: OK - Received message").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
					this.notify();
					break;

				case PacketContent.ACK:
					String ackmsg = content.toString();
					System.out.println(ackmsg);
//					if(ackmsg.equals("ACK: OK - Received request")) {
//						sender.ackReceipt();
//					}
//					if(ackmsg.equals("ACK: OK - Received message")){
//						this.notify();
//					}
					sender.ackReceipt();
					this.notify();
					break;

				default:
					System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
					break;
			}
		}catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Sender Method
	 *
	 */
	public synchronized int start() throws Exception {

		Scanner input = new Scanner(System.in);
		boolean valid = false;
		String destStr = null;
		byte destID = 0;
		String message = null;
		String regex = "^[0-9A-Fa-f]$";
		while(!valid){
			System.out.print("Please enter the destination ID: ");
			destStr = input.next();
			if(destStr.equalsIgnoreCase("quit")){
				return 0;
			}
			if(ID_MAP.containsKey(destStr)){
				destID = ID_MAP.get(destStr);
				valid = true;
			} else{
				System.out.println("-- ID not found.");
			}
		}
		System.out.println("Please enter your message: ");
		message = input.next();
		DatagramPacket request;
		request= new RequestContent(src_id, destID, message).toDatagramPacket();
		request.setSocketAddress(dstAddress);
		sender = new Sender(socket, request);
		sender.start();

		this.wait(90000);
		return 1;
	}


	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			Scanner input = new Scanner(System.in);
			System.out.print("Welcome to the forwarding service system!\nEnter hostname: ");
			String hostname = input.next();
			System.out.print("Enter gateway name: ");
			String gateway = input.next();
			int programStatus = 1;
			Client clt = new Client(gateway, hostname, DEFAULT_DST_PORT, DEFAULT_SRC_PORT);
			while(programStatus == 1){
				programStatus = clt.start();
			}
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
