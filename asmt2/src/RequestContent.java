import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RequestContent extends PacketContent {

    String info;

    /**
     * Constructor that takes in information about a file.
     */
    RequestContent(byte origin, byte dest, String info) {
        packetType= REQUEST;
        this.originID = origin;
        this.destID = dest;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     */
    protected RequestContent(byte origin, byte dest, ObjectInputStream oin) {
        try {
            packetType= REQUEST;
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
