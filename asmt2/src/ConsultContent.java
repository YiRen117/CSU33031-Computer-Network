import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents file information
 *
 */
public class ConsultContent extends PacketContent {

	byte origin, dest;
	String info;

	/**
	 * Constructor that takes in information about a file.
	 */
	ConsultContent(byte origin, byte dest, String info) {
		try {
			packetType = CONSULT;
			this.dest = dest;
			this.origin = origin;
			this.info = info;
		}
        catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Constructs an object out of a datagram packet.
	 */
	protected ConsultContent(byte origin, byte dest, ObjectInputStream oin) {
		try {
			packetType= CONSULT;
			this.originID = origin;
			this.destID = dest;
			info= oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(info);
		}
		catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Returns the info contained in the packet.
	 *
	 * @return Returns the info contained in the packet.
	 */
	public String toString() {
		return info;
	}
}
