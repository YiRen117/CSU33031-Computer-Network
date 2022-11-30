import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HelloContent  extends PacketContent {

    String info;

    /**
     * Constructor that takes in information about a file.
     */
    HelloContent(String info) {
        packetType= HELLO;
        this.info = info;
    }

    /**
     * Constructs an object out of a datagram packet.
     */
    protected HelloContent(ObjectInputStream oin) {
        try {
            packetType= HELLO;
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