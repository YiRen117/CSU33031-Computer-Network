import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents acknowledgements
 *
 */
public class UpdateContent extends PacketContent {

    byte origin, dest;
    String info;

    /**
     * Constructor that takes in information about a file.
     */
    UpdateContent(byte origin, byte dest, String info) {
        try {
            packetType = UPDATE;
            this.dest = dest;
            this.origin = origin;
            this.info = info;
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Constructs an object out of a datagram packet.
     */
    protected UpdateContent(byte origin, byte dest, ObjectInputStream oin) {
        try {
            packetType= UPDATE;
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
