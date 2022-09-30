import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Server extends Node {
	static final int DEFAULT_PORT = 50001;
	static final int DEFAULT_CLI_PORT = 50000;
	static final int DEFAULT_SRC_PORT_TXT = 50002;
	static final int DEFAULT_SRC_PORT_PNG = 50003;
	static final String DEFAULT_DST_NODE = "client";
	static final String DEFAULT_SRC_NODE_TXT = "TXTWorker";
	static final String DEFAULT_SRC_NODE_PNG = "PNGWorker";
	/*
	 *
	 */
	Server(int port) {
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

			if (content.getType()==PacketContent.FILEINFO) {
				System.out.println("File incoming. File info as below:");
				System.out.println("File name: " + ((FileInfoContent)content).getFileName());
				System.out.println("File size: " + ((FileInfoContent)content).getFileSize());

				DatagramPacket response;
				response= new StringPacketContent("ACK: OK - Received file").toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);

				System.out.println("[Sending packet w/ name & length]");
				packet.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_CLI_PORT));
				socket.send(packet);
				System.out.println("[Packet sent]");
			}

			if (content.getType()==PacketContent.STRINGPACKET){
				String incomingString = content.toString();
				if(incomingString.endsWith(".txt") || incomingString.endsWith(".png")){
					System.out.println("Received file request: " + incomingString);

					DatagramPacket response;
					response= new StringPacketContent("ACK: OK - Received request").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					DatagramPacket request;
					request= packet;
					request.setSocketAddress(incomingString.endsWith(".txt") ? new InetSocketAddress(
							DEFAULT_SRC_NODE_TXT, DEFAULT_SRC_PORT_TXT) : new InetSocketAddress(
							DEFAULT_SRC_NODE_PNG, DEFAULT_SRC_PORT_PNG));
					socket.send(request);
				}
				else if(incomingString.startsWith("ACK: ")){
					System.out.println(incomingString);
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		this.wait();
	}

	/*
	 *
	 */
	public static void main(String[] args) {
		try {
			(new Server(DEFAULT_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
