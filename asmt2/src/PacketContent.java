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

	public static final byte REQUEST= 0b00010000; //0x10
	public static final byte CONSULT= 0b00010010; //0x12
	public static final byte TABLEMOD= 0b00010011; //0x13
	public static final byte ACK= 0b00010100; //0x14
	public static final byte UPDATE= 0b00010101; //0x15
	public static final byte HELLO= 0b00010110; //0x16
	public static final byte CONTROLLER= 0b00100000; //0x20
	public static final byte APP1= 0b00100001; //0x21
	public static final byte FW1= 0b00100010; //0x22
	public static final byte APP2= 0b00100011; //0x23
	public static final byte FW2= 0b00100100; //0x24
	public static final byte CP= 0b00100101; //0x25
	public static final byte ISP= 0b00100110; //0x26
	public static final byte SERVER= 0b00100111; //0x27

	byte packetType= 0;
	byte originID= 0;
	byte destID= 0;

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet to analyse.
	 */
	public static PacketContent fromDatagramPacket(DatagramPacket packet) {
		PacketContent content= null;

		try {
			byte packetType, originID, destID;

			byte[] data;
			ByteArrayInputStream bin;
			ObjectInputStream oin;

			data= packet.getData();  // use packet content as seed for stream
			bin= new ByteArrayInputStream(data);
			oin= new ObjectInputStream(bin);

			packetType= oin.readByte();  // read type from beginning of packet
			originID= oin.readByte();
			destID= oin.readByte();

			switch(packetType) {   // depending on type create content object
			case REQUEST:
				content= new RequestContent(originID, destID, oin);
				break;
			case ACK:
				content= new ACKContent(oin);
				break;
			case CONSULT:
				content= new ConsultContent(originID, destID, oin);
				break;
			case TABLEMOD:
				content= new ModContent(oin);
				break;
			case HELLO:
				content= new HelloContent(oin);
				break;
			case UPDATE:
				content= new UpdateContent(originID, destID, oin);
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
			oout.writeByte(originID);
			oout.writeByte(destID);
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

	public byte getOriginID() {
		return originID;
	}

	public byte getDestID() {
		return destID;
	}

}
