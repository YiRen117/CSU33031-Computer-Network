/**
 *
 */
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.FileInputStream;
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

			if (content.getType() == PacketContent.FILEINFO) {
				System.out.println("File incoming. File info as below:");
				System.out.println("File name: " + ((FileInfoContent) content).getFileName());
				System.out.println("File size: " + ((FileInfoContent) content).getFileSize());

				DatagramPacket response;
				response = new StringPacketContent("ACK: OK - Received file").toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
				this.notify();
			}

			if (content.getType() == PacketContent.STRINGPACKET) {
				System.out.println(content.toString());
			}
		}catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Sender Method
	 *
	 */
	public synchronized void start() throws Exception {
		Scanner input = new Scanner(System.in);
		String fname = input.next();

		DatagramPacket request;
		request= new StringPacketContent(fname).toDatagramPacket();
		request.setSocketAddress(dstAddress);
		socket.send(request);

		this.wait();
	}


	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			(new Client(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
