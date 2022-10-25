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

			DatagramPacket response;
			switch (content.getPacketType()) {
				case PacketContent.FILEINFO:
					System.out.println("File incoming. File info as below:");
					System.out.println("File name: " + ((FileInfoContent)content).getFileName());
					System.out.println("File size: " + ((FileInfoContent)content).getFileSize());

					response= new ACKPacketContent("ACK: OK - Received file").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("[Sending packet w/ name & length]");
					packet.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_CLI_PORT));
					socket.send(packet);
					System.out.println("[Packet sent]");
					break;

				case PacketContent.STRINGPACKET:
					System.out.println(content.toString());

					response= new ACKPacketContent("ACK: OK - Received message").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("[Sending packet w/ error message]");
					packet.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_CLI_PORT));
					socket.send(packet);
					System.out.println("[Packet sent]");
					break;

				case PacketContent.ACKPACKET:
					System.out.println(content.toString());
					break;

				case PacketContent.FILEREQUEST:
					String incomingString = content.toString();
					System.out.println("Received file request: " + incomingString);
					response= new ACKPacketContent("ACK: OK - Received request").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					byte fileType = content.getFileType();
					switch (fileType){
						case PacketContent.TXTFILE:
							System.out.println("[Sending file request to TXTWorker]");
							packet.setSocketAddress(new InetSocketAddress(DEFAULT_SRC_NODE_TXT, DEFAULT_SRC_PORT_TXT));
							break;
						case PacketContent.PNGFILE:
							System.out.println("[Sending file request to PNGWorker]");
							packet.setSocketAddress(new InetSocketAddress(DEFAULT_SRC_NODE_PNG, DEFAULT_SRC_PORT_PNG));
							break;
						default:
							System.out.println("File type not supported.");
							break;
					}
					socket.send(packet);
					System.out.println("[Packet sent]");
					break;

				default:
					System.out.println("Problem with package contents. Incoming packet type: " + content.getPacketType());
					break;
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
