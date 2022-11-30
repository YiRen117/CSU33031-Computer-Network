import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ACKContent extends PacketContent {
    String info;

    /**
     * Constructor that takes in information about a file.
     */
    ACKContent(String info) {
        packetType= ACK;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     */
    protected ACKContent(ObjectInputStream oin) {
        try {
            packetType= ACK;
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
