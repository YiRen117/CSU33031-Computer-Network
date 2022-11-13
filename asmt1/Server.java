import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Server extends Node {
	static final int DEFAULT_PORT = 50001;
	static final int DEFAULT_CLI_PORT = 50000;
	static final int DEFAULT_SRC_PORT_TXT = 50002;
	static final int DEFAULT_SRC_PORT_PNG = 50003;
	static final int DEFAULT_SRC_PORT_JPG = 50004;
	static final String DEFAULT_DST_NODE = "client";
	static final String DEFAULT_SRC_NODE_TXT = "TXTWorker";
	static final String DEFAULT_SRC_NODE_PNG = "PNGWorker";
	static final String DEFAULT_SRC_NODE_JPG = "JPGWorker";
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

					System.out.println("[Sent packet w/ name & length]");
					packet.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_CLI_PORT));
					sender = new Sender(socket, packet);
					sender.start();
					break;

				case PacketContent.STRINGPACKET:
					System.out.println(content.toString());

					response= new ACKPacketContent("ACK: OK - Received message").toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);

					System.out.println("[Sent packet w/ error message]");
					packet.setSocketAddress(new InetSocketAddress(DEFAULT_DST_NODE, DEFAULT_CLI_PORT));
					sender = new Sender(socket, packet);
					sender.start();
					break;

				case PacketContent.ACKPACKET:
					System.out.println(content.toString());
					sender.ackReceipt();
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
							System.out.println("[Sent file request to TXTWorker]");
							packet.setSocketAddress(new InetSocketAddress(DEFAULT_SRC_NODE_TXT, DEFAULT_SRC_PORT_TXT));
							break;
						case PacketContent.PNGFILE:
							System.out.println("[Sent file request to PNGWorker]");
							packet.setSocketAddress(new InetSocketAddress(DEFAULT_SRC_NODE_PNG, DEFAULT_SRC_PORT_PNG));
							break;
						case PacketContent.JPGFILE:
							System.out.println("[Sent file request to JPGWorker]");
							packet.setSocketAddress(new InetSocketAddress(DEFAULT_SRC_NODE_JPG, DEFAULT_SRC_PORT_JPG));
							break;
						default:
							System.out.println("File type not supported.");
							break;
					}
					socket.send(packet);
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
		this.wait(90000);
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
