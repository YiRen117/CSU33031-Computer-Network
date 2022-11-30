import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ModContent extends PacketContent {
    String rawString;
    ArrayList<String> info = new ArrayList<>();

    /**
     * Constructor that takes in information about a file.
     */
    ModContent(String rawInfo) {
        packetType= TABLEMOD;
        this.rawString = rawInfo;
    }

    /**
     * Constructs an object out of a datagram packet.
     */
    protected ModContent(ObjectInputStream oin) {
        try {
            packetType= TABLEMOD;
            String rawStr = oin.readUTF();
            String[] neighbours = rawStr.split(",");
            info.addAll(Arrays.asList(neighbours).subList(0, neighbours.length - 1));
        }
        catch(Exception e) {e.printStackTrace();}
    }

    /**
     * Writes the content into an ObjectOutputStream
     *
     */
    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(rawString);
        }
        catch(Exception e) {e.printStackTrace();}
    }


    /**
     * Returns the info contained in the packet.
     *
     * @return Returns the info contained in the packet.
     */
    public String toString() {
        return rawString;
    }

    public ArrayList<String> getTable(){
        return info;
    }

}