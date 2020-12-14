package MerkleAudit;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class MerkleAuditor
{
    private final byte LEAF_HEADER = 0x00;
    private final byte NODE_HEADER = 0x01;
    private final byte LEFT_NODE = 0;
    private final byte RIGHT_NODE = 1;

    public MerkleAuditor()
    {
        connectToServer();
    }

    private void connectToServer()
    {
        System.out.println("Starting audit...");

        try
        {
            LogServerInterface server = (LogServerInterface) Naming.lookup("rmi://localhost:1234/LogServer");
            System.out.println("Connected to server");

            int nLeaves = server.getCurrentEventsSize();
            byte[] rootHash = server.getRootHash();

            System.out.println("Found "+nLeaves+" events on the server");
            System.out.println("Root hash is");

            Helper.showArray(rootHash);

            String event = "An event";
            System.out.println(isMember(server,4,"An event") ? event+" is part of log" : event+ " isn't part of log" );

        }
        catch (Exception e)
        {
            System.out.println("An error occured while trying to connect: "+e.getMessage());
        }
    }

    private boolean isMember(LogServerInterface server,int eventID,String event) throws RemoteException
    {
        //byte[] hash = new byte[];
        byte[] hash = Helper.concatArrays(new byte[]{LEAF_HEADER}, Helper.stringToByte(event));
        //Helper.showArray(hash);

        List<byte[]> path = server.genPath(eventID);

        for(int id = path.size() - 1 ; id  >= 1 ; id--)
        {
            //System.out.println("Merging  with node "+id);
            byte nodePos = path.get(id)[0];
            byte[] nodeHash = Arrays.copyOfRange(path.get(id), 1, path.get(id).length);
            //Helper.showArray(hash);

            if(nodePos == LEFT_NODE)
            {
                hash = computeHash(Helper.concatArrays(nodeHash, hash));
            }
            else
            {
                hash = computeHash(Helper.concatArrays(hash,nodeHash));
            }
        }
        System.out.println("Rebuilt root hash is");
        Helper.showArray(hash);

        return server.getRootHash().equals(hash);
    }

    private byte[] computeHash(byte[] dataToHash)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(dataToHash);
        }
        catch (Exception e)
        {
            System.out.println("No such algorithm "+e.getMessage());
            return null;
        }
    }
}
