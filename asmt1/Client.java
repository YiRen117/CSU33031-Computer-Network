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
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "server";
	InetSocketAddress dstAddress;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Client(String dstHost, int dstPort, int srcPort) {
		try {
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
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
				case PacketContent.FILEINFO:
					System.out.println("File incoming. File info as below:");
					System.out.println("File name: " + ((FileInfoContent) content).getFileName());
					System.out.println("File size: " + ((FileInfoContent) content).getFileSize());

					response = new ACKPacketContent("ACK: OK - Received file").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
					this.notify();
					break;

				case PacketContent.ACKPACKET:
					System.out.println(content.toString());
					sender.ackReceipt();
					break;

				case PacketContent.STRINGPACKET:
					System.out.println(content.toString());
					response = new ACKPacketContent("ACK: OK - Received message").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
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
		String fname = null;
		String[] fnsplit = null;
		while(!valid){
			System.out.print("Please enter the full file name for searching: ");
			fname = input.next();
			if(fname.equalsIgnoreCase("quit")){
				return 0;
			}
			fnsplit = fname.split("\\.");
			if(fnsplit.length == 2 && (!fnsplit[0].equals("")) && (fnsplit[1].equalsIgnoreCase("txt") ||
					fnsplit[1].equalsIgnoreCase("png") || fnsplit[1].equalsIgnoreCase("jpg"))){
				valid = true;
			}
			else{
				System.out.println("-- File name invalid.");
			}
		}
		DatagramPacket request;
		if(fnsplit[1].equalsIgnoreCase("txt")){
			request= new FileRequestContent(fname, PacketContent.TXTFILE).toDatagramPacket();
		}
		else if(fnsplit[1].equalsIgnoreCase("png")){
			request= new FileRequestContent(fname, PacketContent.PNGFILE).toDatagramPacket();
		}
		else{
			request= new FileRequestContent(fname, PacketContent.JPGFILE).toDatagramPacket();
		}
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
			System.out.println("Welcome to the file searching system!");
			int programStatus = 1;
			Client clt = new Client(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT);
			while(programStatus == 1){
				programStatus = clt.start();
			}
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
