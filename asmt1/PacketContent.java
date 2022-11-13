import java.net.DatagramPacket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * The class is the basis for packet contents of various types.
 *
 *
 */
public abstract class PacketContent {

	public static final byte STRINGPACKET= 0b00010000; //0x10
	public static final byte ACKPACKET= 0b00010001; //0x11
	public static final byte FILEREQUEST= 0b00010010; //0x12
	public static final byte FILEINFO= 0b00010011; //0x13
	public static final byte NONFILE= 0b00100000; //0x20
	public static final byte TXTFILE= 0b00100001; //0x21
	public static final byte PNGFILE= 0b00100010; //0x22
	public static final byte JPGFILE= 0b00100011; //0x23

	byte packetType= 0;
	byte fileType= 0;

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet to analyse.
	 */
	public static PacketContent fromDatagramPacket(DatagramPacket packet) {
		PacketContent content= null;

		try {
			byte packetType, fileType;

			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;

			data= packet.getData();  // use packet content as seed for stream
			bin= new ByteArrayInputStream(data);
			oin= new ObjectInputStream(bin);

			packetType= oin.readByte();  // read type from beginning of packet
			fileType= oin.readByte();

			switch(packetType) {   // depending on type create content object
			case STRINGPACKET:
				content= new StringPacketContent(oin);
				break;
			case ACKPACKET:
				content= new ACKPacketContent(oin);
				break;
			case FILEREQUEST:
				content= new FileRequestContent(oin, fileType);
				break;
			case FILEINFO:
				content= new FileInfoContent(oin);
				break;
			default:
				content= null;
				break;
			}
			oin.close();
			bin.close();

		}
		catch(Exception e) {e.printStackTrace();}

		return content;
	}


	/**
	 * This method is used to transform content into an output stream.
	 *
	 * @param out Stream to write the content for the packet to.
	 */
	protected abstract void toObjectOutputStream(ObjectOutputStream out);

	/**
	 * Returns the content of the object as DatagramPacket.
	 *
	 * @return Returns the content of the object as DatagramPacket.
	 */
	public DatagramPacket toDatagramPacket() {
		DatagramPacket packet= null;

		try {
			ByteArrayOutputStream bout;
			ObjectOutputStream oout;
			byte[] data;

			bout= new ByteArrayOutputStream();
			oout= new ObjectOutputStream(bout);

			oout.writeByte(packetType);         // write type to stream
			oout.writeByte(fileType);
			toObjectOutputStream(oout);  // write content to stream depending on type

			oout.flush();
			data= bout.toByteArray(); // convert content to byte array

			packet= new DatagramPacket(data, data.length); // create packet from byte array
			oout.close();
			bout.close();
		}
		catch(Exception e) {e.printStackTrace();}

		return packet;
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public abstract String toString();

	/**
	 * Returns the type of the packet.
	 *
	 * @return Returns the type of the packet.
	 */
	public byte getPacketType() {
		return packetType;
	}

	public byte getFileType() {
		return fileType;
	}

}
