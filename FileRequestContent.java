import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileRequestContent extends PacketContent {

    String info;

    /**
     * Constructor that takes in information about a file.
     * @param filename Initial filename.
     * @param size Size of filename.
     */
    FileRequestContent(String info, byte ftype) {
        packetType= FILEREQUEST;
        fileType= ftype;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     * @param packet Packet that contains information about a file.
     */
    protected FileRequestContent(ObjectInputStream oin, byte ftype) {
        try {
            packetType= FILEREQUEST;
            fileType = ftype;
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
